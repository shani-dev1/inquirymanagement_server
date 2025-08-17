# Inquiry Management System - Server

## 📌 Overview
Java-based server for managing customer inquiries in a multi-client environment.  
Handles automated inquiry processing, representative assignment, background maintenance processes, and file-based persistence.

## 🚀 Key Features
- **Client-Server Architecture:** Multi-tier server handling multiple clients concurrently.  
- **Automated Inquiry Handling:** Assigns inquiries to available representatives with realistic processing times (Questions, Requests, Complaints).  
- **Background Processes:** Nightly maintenance for file cleanup and data organization.  
- **Representative Management:** Full CRUD operations for service representatives.  
- **Data Persistence:** File-based storage (TXT & CSV) using Reflection.  
- **Multi-threading:** Concurrent request processing and thread-safe operations.  
- **Extensible Design:** Modular architecture allows easy addition of new features or inquiry types.

## 🛠️ Tech Stack
- **Language:** Java 8+  
- **Networking:** Socket Programming, Object Serialization  
- **OOP & Patterns:** Factory, Strategy, Observer, Producer-Consumer  
- **Concurrency:** Multi-threaded request handling, Thread Pool Management  
- **Persistence:** File I/O with Reflection (TXT & CSV formats)

## ▶️ How to Run

### 1. Clone the repository
```bash
git clone https://github.com/your-username/InquiryManagementServer.git
cd InquiryManagementServer
