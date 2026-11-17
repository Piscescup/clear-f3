"""Exercise selection, artifact completeness, checksums and release validation."""

import hashlib
import json
from pathlib import Path
import tempfile
import unittest
from unittest.mock import patch

import ci


class CiTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="clear-f3-ci-")
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.write("gradle.properties", "version=1.2.3\nminecraft_version=26.3\nneoforge_version=26.3.0.26-beta\n")
        self.write("fabric/versions.json", json.dumps({"versions": ["1.20.1", "26.3"]}))
        self.write("fabric/versions/mainProject", "26.3\n")
        for minecraft, api in (("1.20.1", "0.92.6+1.20.1"), ("26.3", "0.166.2+26.3")):
            self.write(f"fabric/versions/{minecraft}/gradle.properties",
                       f"minecraft_version={minecraft}\nfabric_api_version={api}\n")
        self.write("CHANGELOG.md", "# Changelog\n\n## 1.2.3\n\n### Features\n\n- Current release.\n\n## 1.2.2\n\n- Old release.\n")

    def write(self, relative, content):
        path = self.root / relative
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content, encoding="utf-8")
        return path

    def downloaded(self):
        for target in ci.targets(self.root):
            self.write(f"build/targets/{target['artifact']}/{target['filename']}", target["id"])

    def test_matrix_covers_both_loaders_and_checks_common_on_primary_fabric(self):
        targets = ci.targets(self.root)
        self.assertEqual([target["id"] for target in targets], ["fabric:1.20.1", "fabric:26.3", "neoforge:26.3"])
        self.assertEqual([target["common"] for target in targets], [False, True, False])
        self.assertEqual(targets[0]["task"], ":fabric:1.20.1:build")
        self.assertEqual(targets[-1]["task"], ":neoforge:build")
        self.assertEqual([target["filename"] for target in targets], [
            "clean-f3-v1.2.3-fabric-0.92.6+1.20.1mc.jar",
            "clean-f3-v1.2.3-fabric-0.166.2+26.3mc.jar",
            "clean-f3-v1.2.3-mc-26.3-neoforge-26.3.0.26-beta.jar",
        ])

    def test_selection_supports_loader_names_explicit_targets_and_deduplication(self):
        self.assertEqual(len(ci.targets(self.root, "fabric, fabric:26.3")), 2)
        self.assertEqual([target["loader"] for target in ci.targets(self.root, "neoforge")], ["neoforge"])
        self.assertEqual(len(ci.targets(self.root, "fabric:1.20.1,neoforge:26.3")), 2)

    def test_unknown_or_unsafe_target_is_rejected(self):
        for selection in ("1.20.1", "fabric:99.9", "fabric:../../outside", "fabric,,neoforge", ":common:publish"):
            with self.subTest(selection=selection), self.assertRaises(ValueError):
                ci.targets(self.root, selection)

    def test_unsafe_version_component_is_rejected(self):
        self.write("gradle.properties", "version=../../outside\nminecraft_version=26.3\nneoforge_version=26.3.0\n")
        with self.assertRaises(ValueError):
            ci.targets(self.root)

    def test_collect_selects_current_installable_jar_only(self):
        target = ci.targets(self.root)[0]
        self.write(f"fabric/versions/1.20.1/build/libs/{target['filename']}", "installable")
        self.write("fabric/versions/1.20.1/build/libs/example-sources.jar", "sources")
        result = ci.collect(self.root, "fabric", "1.20.1")
        self.assertEqual(result.name, target["filename"])
        self.assertEqual(result.read_text(encoding="utf-8"), "installable")
        self.assertEqual(len(list((self.root / "build/ci").rglob("*.jar"))), 1)

    def test_missing_or_empty_jar_stops_collection(self):
        with self.assertRaises(ValueError):
            ci.collect(self.root, "neoforge", "26.3")
        target = ci.targets(self.root)[-1]
        self.write(f"neoforge/build/libs/{target['filename']}", "")
        with self.assertRaises(ValueError):
            ci.collect(self.root, "neoforge", "26.3")

    def test_gather_preserves_loaders_and_writes_verifiable_relative_checksums(self):
        self.downloaded()
        with patch.dict("os.environ", {"GITHUB_STEP_SUMMARY": ""}):
            paths = ci.gather(self.root)
        self.assertEqual({path.parent.name for path in paths}, {"fabric", "neoforge"})
        directory = self.root / "build/releases"
        for line in (directory / "SHA256SUMS").read_text(encoding="utf-8").splitlines():
            digest, relative = line.split("  ", 1)
            self.assertEqual(digest, hashlib.sha256((directory / relative).read_bytes()).hexdigest())

    def test_missing_target_prevents_partial_bundle(self):
        with self.assertRaises(ValueError):
            ci.gather(self.root)
        self.assertFalse((self.root / "build/releases").exists())

    def test_unexpected_development_jar_prevents_bundle(self):
        self.downloaded()
        self.write("build/targets/unexpected/example-dev.jar", "development")
        with self.assertRaises(ValueError):
            ci.gather(self.root)

    def test_release_notes_require_matching_tag_and_only_include_current_version(self):
        self.assertEqual(ci.release_notes(self.root, "v1.2.3"), "### Features\n\n- Current release.\n")
        self.assertEqual(ci.release_notes(self.root, "1.2.3"), ci.release_notes(self.root, "v1.2.3"))
        with self.assertRaises(ValueError):
            ci.release_notes(self.root, "v9.9.9")

    def test_missing_changelog_section_is_rejected(self):
        self.write("CHANGELOG.md", "# Changelog\n\n## 1.2.2\n\n- Older release.\n")
        with self.assertRaises(ValueError):
            ci.release_notes(self.root, "v1.2.3")

    def test_dated_bracketed_release_heading_keeps_subheadings(self):
        self.write("CHANGELOG.md", "# Changelog\n\n## [1.2.3] - 2026-11-17\n\n### Features\n\n- Current release.\n")
        self.assertEqual(ci.release_notes(self.root, "v1.2.3"), "### Features\n\n- Current release.\n")

    def test_empty_release_section_does_not_borrow_older_notes(self):
        self.write("CHANGELOG.md", "# Changelog\n\n## 1.2.3\n\n## 1.2.2\n\n- Older release.\n")
        with self.assertRaises(ValueError):
            ci.release_notes(self.root, "v1.2.3")

    def test_release_assets_use_flat_checksums_for_github_downloads(self):
        self.downloaded()
        with patch.dict("os.environ", {"GITHUB_STEP_SUMMARY": ""}):
            ci.gather(self.root)
        paths = ci.release_assets(self.root)
        directory = self.root / "build/release-assets"
        self.assertEqual(len(paths), 3)
        for line in (directory / "SHA256SUMS").read_text(encoding="utf-8").splitlines():
            digest, filename = line.split("  ", 1)
            self.assertNotIn("/", filename)
            self.assertEqual(digest, hashlib.sha256((directory / filename).read_bytes()).hexdigest())

    def test_publish_matrix_pairs_each_installable_jar_with_both_platforms(self):
        self.downloaded()
        paths = ci.gather(self.root)
        matrix = ci.publish_matrix(self.root, "both")["include"]
        self.assertEqual(len(matrix), 6)
        self.assertEqual({self.root / job["file"] for job in matrix}, set(paths))
        for path in paths:
            jobs = [job for job in matrix if self.root / job["file"] == path]
            self.assertEqual({job["platform"] for job in jobs}, {"curseforge", "modrinth"})
            self.assertEqual(len({job["version"] for job in jobs}), 1)
        self.assertEqual(len({job["version"] for job in matrix}), len(paths))

    def test_publish_matrix_limits_a_manual_upload_to_the_selected_platform_and_target(self):
        self.assertEqual(ci.publish_matrix(self.root, "modrinth", "fabric:26.3")["include"], [{
            "platform": "modrinth",
            "loader": "fabric",
            "minecraft": "26.3",
            "file": "build/releases/fabric/clean-f3-v1.2.3-fabric-0.166.2+26.3mc.jar",
            "version": "1.2.3+mc26.3-fabric",
        }])


if __name__ == "__main__":
    unittest.main()
