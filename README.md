# task-tracker
An easy interface for updating task-tracker

> *Note:* It will not have any release, as it was created for personal usage, however for future improvements and regarding this might help others, it is being shared.

<details>
<summary>

## Details

</summary>

# 📱 App Documentation

Welcome to **Excel Updater**, the app designed to make logging your day effortless and stylish. 🚀 This Android app collects user inputs and seamlessly updates an Excel file stored on OneDrive. With sleek design, adding/deleting topics, and customizable notifications, it’s built to fit your productivity needs.

---

## 🌟 Features at a Glance

| Feature                        | Description                                                                                 |
|--------------------------------|---------------------------------------------------------------------------------------------|
| ✏️ **Input Fields**            | Collects user data (date, time, dropdowns, and descriptions). Auto-fills smart defaults.     |
| 🔔 **Notifications**           | Reminds you to log entries with intervals of 30 minutes or 1 hour. Easily configurable.     |
| 🔑 **Secure Login**            | Uses OAuth for safe Microsoft login via an embedded WebView.                                |
| 📊 **Excel Integration**       | Updates your OneDrive Excel sheet with a single tap.                                        |
| 🎨 **Modern UI**               | Features gradient backgrounds, intuitive input boxes, and an elevated submit button.        |

---

## 🛠️ Prerequisites for Developers

Make sure you have the following ready:  
- **Android Studio** installed.  
- **Android SDK** version **X.X** or higher.  
- **Microsoft account** with OneDrive access.  
- OAuth **client ID** and **redirect URL**.

---

## 🚀 Setup Instructions

1. **Clone the Repository**  
   ```bash
   git clone <repository-url>
   ```

2. **Open in Android Studio**  
   Launch Android Studio and load the project folder.

3. **Configure OAuth**  
   Replace placeholders with your credentials:  
   ```xml
   <string name="client_id">YOUR_CLIENT_ID</string>
   <string name="excel sheet id">EXCEL_ID</string>
   ```

4. **Run the App**  
   Hit the **Run** ▶️ button in Android Studio to launch the app on your device or emulator.

---

## 🖌️ App Design Details

### **UI Breakdown**
| Component                 | Purpose                                                     |
|---------------------------|-------------------------------------------------------------|
| 🌐 **WebView**            | Handles secure login using Microsoft OAuth.                 |
| 📝 **ScrollView**         | Contains input fields for user data collection.             |
| ⚡ **Button**             | Submits data to OneDrive Excel. Distinct and visually clear. |
| 📊 **Progress Bar**       | Displays upload progress in real-time.                      |

### **Styling Highlights**
- Gradient backgrounds (`blue_gradient`) for a sleek look.  
- Inputs are grouped in **boxed containers** for clarity.  
- Elevated `Button` with custom colors for contrast and focus.

---

## 🛡️ Usage Guidelines

1. **Log In**  
> This fileld needs work to do, it's written based on just personal use
   Open the app and sign in with your Microsoft account.

2. **Fill Details**  
   - `input1`: Auto-fills the date.  
   - `input2` & `input3`: Auto-populates start and end times.  
   - `input4` & `input5`: Choose topics from dropdown menus or add new ones.  
   - `input6`: Add optional notes.

3. **Update Excel**  
   Tap **Update Excel** to save your inputs to the OneDrive file.

---

## 📂 Folder Structure

| Directory             | Contents                                                      |
|-----------------------|--------------------------------------------------------------|
| **`/res/drawable/`**  | Custom gradients, shapes, and icons.                         |
| **`/res/layout/`**    | All XML layout files for the app’s screens.                  |
| **`/src/main/java/`** | Core app logic, including OAuth and OneDrive integrations.   |

---

## 📸 Screenshots  
> to-do

---

## 💡 Future Enhancements

| Planned Improvement                              | Description                                                |
|-------------------------------------------------|------------------------------------------------------------|
| ⚙️ Add **Settings Page**                        | Manage user preferences, like notification intervals.       |
| 📋 Dynamic Dropdown Options                     | Allow users to create/delete dropdown entries on the fly.   |
| 🌐 Better Error Handling                        | Improve user feedback on network or API issues.            |
| 🛑 Notification Pause                           | Add the ability to temporarily disable notifications.       |
| 📈 Analytics Dashboard                          | Provide usage statistics and data visualization.            |
| 🔒 Two-Factor Authentication                    | Enhance login security with an additional verification step.|
| 📂 Multi-Excel File Support                     | Enable managing multiple Excel files within the app.        |
| 🔊 Voice Input                                  | Allow users to fill inputs via speech-to-text.              |
| 🎨 Enhanced Customization                       | Let users change themes, fonts, and color schemes.          |

and many more!

---

## 🤝 Contribution Guidelines

1. Fork the repository.  
2. Create a new branch for your feature.  
3. Submit a pull request with detailed descriptions.

---

## 🔗 Resources & Dependencies

- OAuth Library: `ADD_DEPENDENCY_HERE`  
- Microsoft Graph API: `ADD_DEPENDENCY_HERE`

---

## 📜 Dedicated to 

`traitors`


</details>