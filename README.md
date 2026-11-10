# Ready to Start Withdrawal (Android)

A simple Android timer with two automatic phases:

1. **Ready to Start Withdrawal** — counts down to **12 November 2026, 6:00 PM**.
2. **Congratulations you did it!** — after the target, counts elapsed time as `Days:Hours:Minutes:Seconds.tenths`.

The target is interpreted as Sri Lanka local time (`Asia/Colombo`, UTC+05:30). The timer updates while the app is open. It calculates from the device clock, so the value catches up accurately when the app is reopened.

## Stop behavior
Tap **STOP TIMER** and confirm to freeze the display. The stopped state is saved and remains stopped after closing/reopening the app. This is a one-way stop; reinstalling/clearing app data resets it.

## Build the APK
This source project includes a GitHub Actions workflow.

1. Create a new GitHub repository and upload the contents of this folder.
2. Open the repository's **Actions** tab.
3. Run **Build Android APK** (or push to the `main` branch).
4. Open the completed workflow run and download the artifact named `ReadyToStartWithdrawal-debug-apk`.
5. Extract `app-debug.apk` and install it on your Android phone. Android may ask you to allow installation from that source.

The APK is a debug-signed installable build. No special permissions or internet connection are required by the app.
