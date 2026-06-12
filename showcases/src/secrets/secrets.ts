import { EditorSettingsModel } from '@imgly/editor-react-native';

export class Secrets {
    static license = "";

    /**
     * Optional override for the editor base URI, populated by
     * `configure_secrets.sh` from the `SHOWCASES_BASE_URI` env var in CI to
     * test against per-branch staging deployments. When empty,
     * [EditorSettingsModel] uses its built-in default.
     */
    static baseUri = "";

    /**
     * Builds [EditorSettingsModel] from these secrets, applying the optional
     * baseUri override when present.
     */
    static editorSettings(): EditorSettingsModel {
        return Secrets.baseUri
            ? new EditorSettingsModel({ license: Secrets.license, baseUri: Secrets.baseUri })
            : new EditorSettingsModel({ license: Secrets.license });
    }

    /**
     * Builds [EditorSettingsModel] with `license: undefined` (evaluation mode
     * with watermark), still applying the optional baseUri override when
     * present. Used by examples that intentionally test nil-license behavior.
     */
    static evaluationModeSettings(): EditorSettingsModel {
        return Secrets.baseUri
            ? new EditorSettingsModel({ license: undefined, baseUri: Secrets.baseUri })
            : new EditorSettingsModel({ license: undefined });
    }
}
