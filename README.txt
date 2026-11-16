FMHY app - WebView wrapper for fmhy.net with domain-based ad blocking.

BUILD OPTION A - Android Studio: File > Open this folder, wait for sync, Build > Build APK(s).
BUILD OPTION B - GitHub (no install): upload this folder to a new GitHub repo,
  go to Actions > Build APK > Run workflow, download "fmhy-apk" artifact, unzip, install app-debug.apk.
Edit app/src/main/assets/blocklist.txt to add more blocked domains.
