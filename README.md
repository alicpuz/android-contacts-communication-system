# Contacts Intent System (Android)

---

## 🧩 Architecture

The system is composed of:

### 1. Contacts Application
- User-facing application
- Sends requests to retrieve contacts
- Receives encrypted data (or error messages)

### 2. Contacts Provider
- Background/receiver application
- Handles contacts access
- Requests runtime permissions
- Reads device contacts
- Encrypts data before sending it back

---

## 🔄 Communication Flow

1. User clicks "Request Contacts" in Contacts Application
2. Application sends Intent: android.intent.action.CONTACTS
3. Contacts Provider receives request
4. Provider checks `READ_CONTACTS` permission
5. If granted:
- Reads contacts from device
- Encrypts data
- Sends result back to Contacts Application
6. If denied:
- Sends error response or redirects user

---

## 🔐 Features

- Inter-app communication using Intents
- Runtime permission handling (READ_CONTACTS)
- Access to Android Contacts Provider
- Data encryption before transfer

---

## 🛠 Technologies

- Java
- Android SDK
- Intents (explicit & implicit)
- Android Permissions system
- ContentResolver (Contacts access)
- Encryption (CryptographyManager)

---

## ▶ How to run

1. Install both applications on emulator/device:
- Contacts Provider
- Contacts Application

2. Launch **Contacts Application**

3. Click:
- "Request Contacts"

4. Grant permissions when prompted

---

## ⚠️ Important Notes

- Both applications must be installed for full functionality
- If Contacts Provider is missing, request will fail gracefully
- Requires `READ_CONTACTS` permission

---

## 🎓 Purpose

This project was created as part of university coursework to demonstrate:
- Android inter-application communication
- Secure data handling
- Permissions management
- System-level Android components interaction