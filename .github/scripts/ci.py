"""Build-matrix and artifact helpers for Clear F3's GitHub workflows."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil


ROOT = Path(__file__).resolve().parents[2]


def properties(path):
    result = {}
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if line and not line.startswith(("#", "!")) and "=" in line:
            key, value = line.split("=", 1)
            result[key.strip()] = value.strip()
    return result


def component(value):
    if not re.fullmatch(r"[0-9][0-9A-Za-z.+_-]*", value):
        raise ValueError(f"Invalid version component: {value!r}")
    return value


def targets(root, selection=""):
    common = properties(root / "gradle.properties")
    version = component(common["version"])
    primary = (root / "fabric/versions/mainProject").read_text(encoding="utf-8").strip()
    versions = json.loads((root / "fabric/versions.json").read_text(encoding="utf-8"))["versions"]
    if not versions or len(set(versions)) != len(versions) or primary not in versions:
        raise ValueError("Fabric targets must be unique and include the main project")
    result = []
    for minecraft in versions:
        if not re.fullmatch(r"[0-9]+\.[0-9]+(?:\.[0-9]+)?", minecraft):
            raise ValueError(f"Invalid Minecraft target: {minecraft!r}")
        specific = properties(root / "fabric/versions" / minecraft / "gradle.properties")
        if specific["minecraft_version"] != minecraft:
            raise ValueError(f"Mismatched Minecraft properties for {minecraft}")
        result.append({"loader": "fabric", "minecraft": minecraft,
                       "dependency": component(specific["fabric_api_version"]),
                       "common": minecraft == primary})
    result.append({"loader": "neoforge", "minecraft": component(common["minecraft_version"]),
                   "dependency": component(common["neoforge_version"]), "common": False})
    for target in result:
        loader, minecraft = target["loader"], target["minecraft"]
        target["id"] = f"{loader}:{minecraft}"
        target["task"] = f":fabric:{minecraft}:build" if loader == "fabric" else ":neoforge:build"
        target["artifact"] = f"clear-f3-target-{loader}-{minecraft}"
        if loader == "fabric":
            target["filename"] = f"clean-f3-v{version}-fabric-{target['dependency']}mc.jar"
        else:
            target["filename"] = f"clean-f3-v{version}-mc-{minecraft}-{loader}-{target['dependency']}.jar"
    if len({target["filename"] for target in result}) != len(result):
        raise ValueError("Multiple build targets produce the same release filename")
    if selection.strip():
        selected = set()
        for selector in selection.split(","):
            selector = selector.strip()
            matches = [target["id"] for target in result
                       if selector in (target["loader"], target["id"])]
            if not matches:
                raise ValueError(f"Unknown target: {selector!r}")
            selected.update(matches)
        result = [target for target in result if target["id"] in selected]
    return result


def output(name, value):
    print(f"{name}={value}")
    if destination := os.environ.get("GITHUB_OUTPUT"):
        with open(destination, "a", encoding="utf-8") as stream:
            stream.write(f"{name}={value}\n")


def require_files(paths):
    for path in paths:
        if not path.is_file() or path.stat().st_size == 0:
            raise ValueError(f"Missing or empty installable JAR: {path}")


def copy_files(paths, destinations):
    require_files(paths)
    for source, destination in zip(paths, destinations):
        destination.parent.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(source, destination)


def checksums(directory, paths):
    lines = []
    for path in sorted(paths):
        digest = hashlib.sha256(path.read_bytes()).hexdigest()
        lines.append(f"{digest}  {path.relative_to(directory).as_posix()}")
    (directory / "SHA256SUMS").write_text("\n".join(lines) + "\n", encoding="utf-8")


def collect(root, loader, minecraft):
    target = next((target for target in targets(root) if target["id"] == f"{loader}:{minecraft}"), None)
    if target is None:
        raise ValueError(f"Unknown target: {loader}:{minecraft}")
    module = root / "fabric/versions" / minecraft if loader == "fabric" else root / "neoforge"
    destination = root / "build/ci" / loader / target["filename"]
    copy_files([module / "build/libs" / target["filename"]], [destination])
    return destination


def gather(root, selection=""):
    chosen = targets(root, selection)
    source = root / "build/targets"
    sources = [source / target["artifact"] / target["filename"] for target in chosen]
    if set(source.rglob("*.jar")) != set(sources):
        raise ValueError("Downloaded JARs do not match the selected build matrix")
    directory = root / "build/releases"
    destinations = [directory / target["loader"] / target["filename"] for target in chosen]
    copy_files(sources, destinations)
    checksums(directory, destinations)
    if summary := os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(summary, "a", encoding="utf-8") as stream:
            stream.write("## Clear F3 build artifacts\n\n| Loader | Minecraft | JAR |\n| --- | --- | --- |\n")
            for target in chosen:
                stream.write(f"| {target['loader']} | {target['minecraft']} | `{target['filename']}` |\n")
            stream.write("\nDownload **clear-f3-mod-jars** for the loader directories and SHA256SUMS.\n")
    return destinations


def release_notes(root, tag):
    version = component(properties(root / "gradle.properties")["version"])
    if tag not in (version, f"v{version}"):
        raise ValueError(f"Release tag must be {version!r} or {'v' + version!r}, got {tag!r}")
    changelog = (root / "CHANGELOG.md").read_text(encoding="utf-8")
    pattern = rf"^##[ \t]+(?:\[{re.escape(version)}\]|{re.escape(version)})(?:[ \t]+[^\n]*)?$"
    match = re.search(pattern, changelog, re.MULTILINE)
    if match is None:
        raise ValueError(f"CHANGELOG.md has no section for {version}")
    remaining = changelog[match.end():]
    end = re.search(r"^##[ \t]+", remaining, re.MULTILINE)
    notes = remaining[:end.start()] if end else remaining
    if not notes.strip():
        raise ValueError(f"CHANGELOG.md section for {version} is empty")
    return notes.strip() + "\n"


def release_assets(root):
    directory = root / "build/release-assets"
    chosen = targets(root)
    sources = [root / "build/releases" / target["loader"] / target["filename"] for target in chosen]
    destinations = [directory / target["filename"] for target in chosen]
    copy_files(sources, destinations)
    checksums(directory, destinations)
    return destinations


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    for name in ("matrix", "gather"):
        commands.add_parser(name).add_argument("--targets", default="")
    collect_parser = commands.add_parser("collect")
    collect_parser.add_argument("--loader", required=True, choices=("fabric", "neoforge"))
    collect_parser.add_argument("--minecraft", required=True)
    commands.add_parser("release-info").add_argument("--tag", required=True)
    commands.add_parser("release-assets")
    args = parser.parse_args()
    try:
        if args.command == "matrix":
            fields = ("loader", "minecraft", "task", "artifact", "common")
            matrix = [{field: target[field] for field in fields} for target in targets(ROOT, args.targets)]
            output("matrix", json.dumps({"include": matrix}, separators=(",", ":")))
        elif args.command == "collect":
            output("path", collect(ROOT, args.loader, args.minecraft).relative_to(ROOT).as_posix())
        elif args.command == "gather":
            gather(ROOT, args.targets)
        elif args.command == "release-info":
            notes = release_notes(ROOT, args.tag)
            destination = ROOT / "build/release-notes.md"
            destination.parent.mkdir(parents=True, exist_ok=True)
            destination.write_text(notes, encoding="utf-8")
            output("tag", args.tag)
        else:
            release_assets(ROOT)
    except (KeyError, OSError, ValueError) as error:
        parser.error(str(error))


if __name__ == "__main__":
    main()
