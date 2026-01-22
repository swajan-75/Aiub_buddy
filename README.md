# 📚 AiubBuddy

AiubBuddy is an Android application designed to help **AIUB students** manage their academic life efficiently. It provides **real-time notices, class routines, faculty information, and schedules** with offline support and push notifications — even when the user is not logged in.

This project follows a **modern, scalable mobile + backend architecture** using **Kotlin, Firebase, Room Database, and NestJS**.

---

## ✨ Features

### 🔔 Real-Time Notifications
- Instant university notice alerts using **Firebase Cloud Messaging (FCM)**
- Notifications work even when the app is closed
- Backend triggers notifications only when new data is detected

### 📅 Smart Class Routine
- Add, edit, and remove personal routines
- View daily & weekly class schedules
- Stored locally using **Room (SQLite)** for offline access

### 🧑‍🏫 Faculty Information
- View faculty profiles, departments, and details
- Synced from backend API
- Cached locally for faster access

### 🕒 Access Without Re-Login
- After one-time OTP login, users can:
  - View routine
  - View class time
  - View notices  
  without logging in again
- Implemented using **Firebase token + Room database**

### 🔐 Secure Authentication
- Firebase OTP-based phone authentication
- Secure user sessions

### 📧 Email Support
- SMTP integration for:
  - Verification
  - System alerts
  - Support communication

---

---

## 📱 Android Tech Stack

| Technology | Purpose |
|----------|---------|
| Kotlin | Main application |
| Firebase Auth (OTP) | User authentication |
| Firebase FCM | Push notifications |
| Room (SQLite) | Offline storage |
| Retrofit | API communication |
| MVVM Architecture | Clean code structure |

---

## 🖥 Backend Tech Stack

| Technology | Purpose |
|----------|---------|
| NestJS | Backend framework |
| REST API | Data communication |
| Firebase Admin SDK | FCM + Auth |
| Database (PostgreSQL) | Data storage |
| SMTP | Email service |

---

## 🔄 Real-Time Notification Flow

1. Admin adds a new notice  
2. NestJS saves it to database  
3. Backend compares with previous data  
4. If new → sends notification via **FCM**  
5. Android devices receive instantly  

---

## 🧠 Why AiubBuddy is Modern

| Feature | Traditional Apps | AiubBuddy |
|--------|----------------|----------|
| Notices | Manual refresh | Push notifications |
| Offline | Not available | Room database |
| Login | Every time | Token-based |
| Architecture | Basic | MVVM |
| Backend | Static | Event-driven |

---

## 🚀 Future Improvements
- chat server
- routine alert

---

