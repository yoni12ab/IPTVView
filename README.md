# הטלוויזיה שלי — IPTV Player

Hebrew IPTV player. Filters categories containing **"Hebrew"**, displays all channels in one alphabetically-sorted list.

## GitHub Pages (Web)

1. Push this repo to GitHub
2. Go to **Settings → Pages → Source → main branch / root**
3. Site will be at `https://<YOUR_USERNAME>.github.io/<REPO_NAME>/`

## Android APK

### Prerequisites
- Android Studio (Hedgehog or newer)
- Android SDK 34

### Steps
1. Open the `android/` folder in Android Studio as a project
2. In `MainActivity.kt` replace `APP_URL` with your GitHub Pages URL
3. **Build → Generate Signed Bundle / APK → APK**
4. Install on phone or Android TV box

### Features
- Loads latest version from GitHub Pages on every launch (always up-to-date)
- `localStorage` enabled → login credentials saved, no re-login needed
- Fullscreen video via tap or TV remote OK button
- Back button exits fullscreen, then stops video
- D-pad navigation (↑ ↓ to scroll list, OK to play)
- Works on phone, tablet, and Android TV
