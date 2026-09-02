# Kaslik Kart Rush

Custom Android racing-game fork based on SuperTuxKart.

## Branding
- Developer: Kaslik Noristani
- Brand: Kaslik Services
- Package: `com.kaslikservices.kaslikkartrush`

## GitHub Actions
The repository includes:
- `.github/workflows/android.yml` — ARM64 debug APK artifact.
- `.github/workflows/android-release.yml` — signed ARM64 release AAB/APK; configure `KASLIK_KEYSTORE_BASE64`, `KASLIK_KEYSTORE_PASSWORD`, and `KASLIK_KEY_ALIAS` GitHub Actions secrets first.

## Important licensing note
This is a fork based on GPL-licensed SuperTuxKart code. Keep `COPYING`, attribution/credits, and third-party license files when redistributing the game. Rebranding does not transfer ownership of upstream code/assets or trademarks.

## Monetization
Unity Ads 4.20.0 is wired for interstitial and rewarded placements, with test mode on debug builds. Meta Audience Network 6.22.0 is wired as an optional banner provider. Verify provider account status, consent/privacy configuration, ad-unit ownership, and test mode before publishing.
