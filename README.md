# Sonic Task Manager

> 🚀 A stress-free, intelligent personal task management system that transforms your daily chaos into focused productivity.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-61dafb.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Demo](#-demo)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
- [Project Structure](#-project-structure)
- [API Documentation](#-api-documentation)
- [Architecture](#-architecture)
- [Configuration](#-configuration)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

## 🎯 Overview

Sonic Task Manager is an innovative personal productivity system that revolutionizes task management through intelligent prioritization and a stress-free interface. Unlike traditional todo lists that overwhelm users with endless items, Sonic uses a **workspace metaphor** to present only what matters today.

### Why Sonic Task Manager?

Traditional task management apps suffer from:
- ❌ **Task Overwhelm** - Endless lists create anxiety
- ❌ **Decision Paralysis** - Too many choices, unclear priorities
- ❌ **Lost Long-term Goals** - Important tasks get buried
- ❌ **Rigid Categories** - Not all tasks fit the same mold

Sonic Task Manager solves these with:
- ✅ **Single Focus System** - One primary task at a time
- ✅ **Smart Prioritization** - AI-powered task selection
- ✅ **Gentle Reminders** - Non-intrusive notification system
- ✅ **Flexible Task Types** - Deadlines, habits, reminders, events

## ✨ Key Features

### 🎯 Intelligent Focus Algorithm
```
Focus Score = (Priority × 100) + (Urgency × 80) + (Complexity × 30) + Bonuses
```
- Automatically selects your most important task for today
- Considers multiple factors: deadlines, priority, complexity, progress
- Provides motivational context for each focus task

### 🧠 Smart Auto-Detection
The system intelligently analyzes task titles to:
- **Auto-categorize tasks** (deadline, habit, reminder, event)
- **Estimate complexity** (easy, medium, hard)
- **Support bilingual input** (Vietnamese + English)

Example keywords:
- `"practice"`, `"tập"` → Habit task
- `"call"`, `"gọi"` → Easy complexity
- `"design"`, `"thiết kế"` → Hard complexity

### 📊 Dual Interface Design

#### Main Workspace (Daily Use)
- Single-column, mobile-first layout
- Shows only today's essentials
- One-click task actions
- Stress-free minimal design

#### Task Studio (Power Users)
- YouTube Studio-inspired interface
- Comprehensive task management
- Advanced filtering & search
- Bulk operations support
- Pagination for large datasets

### 😌 Mood-Based Workload Assessment
Daily mood calculation based on stress factors:
```
Stress = (Overdue×3) + (Urgent×2) + (HighPriority×1.5) + (HardTasks×1)
```
Moods: `intense` → `busy` → `active` → `steady` → `chill` → `relaxed`

### 💭 Gentle Reminder System
- Probabilistic surfacing (not aggressive)
- Maximum 3 reminders shown at once
- Flexible snoozing (1 day to 1 month)
- Based on time since last interaction

## 🎬 Demo

### Main Workspace
![workspace-demo](https://via.placeholder.com/800x400/6c5ce7/ffffff?text=Main+Workspace+Demo)

### Task Studio
![studio-demo](https://via.placeholder.com/800x400/a29bfe/ffffff?text=Task+Studio+Demo)

## 🛠️ Tech Stack

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.3.12 | Core framework |
| Java | 17 | Programming language |
| SQLite | 3.45.3 | Embedded database |
| Spring Data JPA | - | ORM layer |
| Maven | 3.6+ | Build tool |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2 | UI framework |
| Vite | 6.3.5 | Build tool |
| React Router | 6.20 | Routing |
| Axios | 1.6.0 | HTTP client |
| CSS3 | - | Modular styling with variables |

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:
- ☕ Java 17 or higher
- 📦 Node.js 18 or higher
- 🔧 Maven 3.6 or higher

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/sonic-task-manager.git
cd sonic-task-manager
```

2. **Start the Backend**
```bash
cd sonic-task-manager-api
mvn spring-boot:run
```
Backend runs at: `http://localhost:8080`

3. **Start the Frontend**
```bash
cd sonic-task-manager-web
npm install
npm run dev
```
Frontend runs at: `http://localhost:5173`

4. **Access the Application**
- Main Workspace: `http://localhost:5173`
- API Documentation: `http://localhost:8080/api`

### Quick Start Guide

1. **First Run**: Application auto-initializes with sample data
2. **View Focus Task**: Your most important task appears prominently
3. **Add New Task**: Click "Add New Task" - type auto-detected
4. **Complete Tasks**: Click ✓ to mark done
5. **Snooze Tasks**: Click 💤 to postpone
6. **Access Studio**: Click "Task Studio" for advanced features

## 📁 Project Structure

```
sonic-task-manager/
│
├── 📦 sonic-task-manager-api/          # Spring Boot Backend
│   ├── src/main/java/com/sonic/taskmanager/
│   │   ├── 🎮 controller/              # REST endpoints
│   │   │   ├── WorkspaceController.java
│   │   │   ├── TaskController.java
│   │   │   ├── StudioController.java
│   │   │   └── PreferencesController.java
│   │   │
│   │   ├── 🧠 service/                 # Business logic
│   │   │   ├── WorkspaceService.java   # Workspace calculations
│   │   │   ├── TaskService.java        # Task operations
│   │   │   ├── FocusCalculator.java    # Focus algorithm
│   │   │   └── MoodCalculatorService.java
│   │   │
│   │   ├── 💾 repository/              # Data access
│   │   │   ├── TaskRepository.java
│   │   │   └── PreferencesRepository.java
│   │   │
│   │   ├── 📊 model/                   # Data models
│   │   │   ├── Task.java
│   │   │   ├── dto/                    # DTOs
│   │   │   ├── request/                # Request objects
│   │   │   └── response/               # Response objects
│   │   │
│   │   └── 🔧 util/                    # Utilities
│   │       ├── DateUtils.java
│   │       └── FocusCalculator.java
│   │
│   ├── src/main/resources/
│   │   └── application.properties      # Configuration
│   └── pom.xml                         # Maven config
│
├── 🎨 sonic-task-manager-web/          # React Frontend
│   ├── src/
│   │   ├── 🧩 components/              # React components
│   │   │   ├── Workspace.jsx           # Main interface
│   │   │   ├── TaskCard.jsx            # Task display
│   │   │   ├── AddTaskForm.jsx         # Task creation
│   │   │   └── studio/                 # Studio components
│   │   │       ├── TasksManagement.jsx
│   │   │       ├── StudioLayout.jsx
│   │   │       └── shared/             # Reusable components
│   │   │
│   │   ├── 🔌 services/                # API services
│   │   │   ├── api.js                  # Axios config
│   │   │   ├── workspaceService.js
│   │   │   ├── taskService.js
│   │   │   └── studioService.js
│   │   │
│   │   ├── 🪝 hooks/                   # Custom hooks
│   │   │   └── useWorkspace.jsx        # State management
│   │   │
│   │   └── 🎨 styles/                  # CSS
│   │       ├── index.css               # Main import file
│   │       ├── variables.css           # CSS variables & utilities
│   │       ├── workspace.css           # Workspace interface
│   │       ├── studio.css              # Studio interface
│   │       └── responsive.css          # Mobile-first responsive
│   │
│   ├── package.json                    # NPM config
│   └── vite.config.js                  # Vite config
│
└── 💾 data/
    └── database.db                     # SQLite database
```

## 📡 API Documentation

### Response Format
All APIs return standardized responses:
```json
{
  "success": true,
  "message": "Operation completed",
  "timestamp": "2024-01-10T10:30:00",
  "data": { ... }
}
```

### Core Endpoints

#### 🏠 Workspace APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/workspace` | Get today's calculated workspace |
| `POST` | `/api/workspace/refresh` | Recalculate workspace |
| `PUT` | `/api/workspace/reminders/{id}/snooze` | Snooze reminder |

#### 📝 Task APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/tasks` | Get all active tasks |
| `POST` | `/api/tasks` | Create new task |
| `PUT` | `/api/tasks/{id}` | Update task |
| `DELETE` | `/api/tasks/{id}` | Delete task |
| `PUT` | `/api/tasks/{id}/complete` | Mark as complete |
| `PUT` | `/api/tasks/{id}/snooze` | Snooze task |
| `GET` | `/api/tasks/quick-wins` | Get quick win tasks |

#### 🎨 Studio APIs
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/studio/tasks?page=0&size=20` | Paginated tasks |
| `POST` | `/api/studio/tasks/bulk-update` | Bulk operations |
| `GET` | `/api/studio/stats` | Task statistics |

### Request Examples

#### Create Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Design new feature",
    "description": "Create mockups for user dashboard",
    "priority": "high",
    "deadline": "2024-12-31"
  }'
```

#### Bulk Complete Tasks
```bash
curl -X POST http://localhost:8080/api/studio/tasks/bulk-update \
  -H "Content-Type: application/json" \
  -d '{
    "taskIds": [1, 2, 3],
    "operation": "complete"
  }'
```

## 🏗️ Architecture

### Design Patterns

| Pattern | Usage |
|---------|-------|
| **Repository Pattern** | Clean data access separation |
| **DTO Pattern** | API contract isolation |
| **Response Wrapper** | Consistent API responses |
| **Optimistic Updates** | Better UX with immediate feedback |
| **Context Pattern** | Centralized state management |

### Key Algorithms

#### Focus Score Algorithm
```java
public double calculateFocusScore(Task task) {
    double score = 0;
    score += getPriorityWeight(task.getPriority()) * 100;
    score += getUrgencyWeight(task) * 80;
    score += getComplexityWeight(task.getComplexity()) * 30;
    if ("doing".equals(task.getStatus())) score += 50;
    if (task.getProgressCurrent() > 0) score += 20;
    return score;
}
```

#### Mood Calculation
```java
public String calculateDailyMood(List<Task> tasks) {
    double stressScore = 0;
    stressScore += overdueCount * 3;
    stressScore += urgentCount * 2;
    stressScore += highPriorityCount * 1.5;
    // Returns: intense | busy | active | steady | chill | relaxed
}
```

### Database Schema

#### Tasks Table
```sql
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(50),
    priority VARCHAR(20),
    complexity VARCHAR(20),
    status VARCHAR(20) DEFAULT 'todo',
    deadline DATE,
    parent_id INTEGER,
    progress_current INTEGER DEFAULT 0,
    progress_total INTEGER DEFAULT 1,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES tasks(id)
);
```

## ⚙️ Configuration

### Backend Configuration
`application.properties`:
```properties
# Database
spring.datasource.url=jdbc:sqlite:./data/database.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# JPA
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update

# Server
server.port=8080

# CORS
cors.allowed-origins=http://localhost:5173
```

### Frontend Configuration
`vite.config.js`:
```javascript
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

## 📈 Roadmap

### **Phase 1: Complete Studio Interface** ⭐ *Current Focus*
- [x] Core task management
- [x] Focus algorithm
- [x] Studio interface foundation
- [x] Bulk operations
- [ ] Detailed task editor with full metadata
- [ ] Habit management interface with session tracking
- [ ] Reminder management center
- [ ] Advanced filtering & search
- [ ] Task detail views
- [ ] Progress visualization

### **Phase 2: Brain Dump System** 💡 *Next Major Feature*
- [ ] Quick capture interface ("What's on your mind?")
- [ ] Unified collection for Ideas/Problems/Random thoughts
- [ ] Smart categorization system
- [ ] Transform engine: Ideas → Tasks, Problems → Solutions
- [ ] Mobile-optimized capture

### **Phase 3: Intelligence Layer** 🤖 *Smart Enhancements*
- [ ] Weather integration for task suggestions
- [ ] Calendar sync for optimal scheduling
- [ ] AI problem analysis and suggestions
- [ ] Smart task breakdown assistance
- [ ] Time pattern recognition

### **Phase 4: Advanced Analytics** 📊 *Optimization*
- [ ] Focus effectiveness tracking
- [ ] Enhanced focus algorithm
- [ ] Habit success analytics
- [ ] Personal productivity patterns
- [ ] Gentle coaching system

### **Phase 5: Platform Enhancement** 🚀 *Scaling*
- [ ] Progressive Web App (PWA)
- [ ] Calendar deep integration
- [ ] Voice capture
- [ ] Advanced automation
- [ ] Custom themes and workflows

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines

- Follow Java naming conventions
- Write unit tests for new features
- Update documentation for API changes
- Use meaningful commit messages
- Ensure code passes linting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Inspired by the need for calmer productivity tools
- Built with ❤️ for everyone struggling with task overwhelm
- Thanks to the Spring Boot and React communities

## 📞 Contact

- **Project Link**: [https://github.com/yourusername/sonic-task-manager](https://github.com/yourusername/sonic-task-manager)
- **Issues**: [GitHub Issues](https://github.com/yourusername/sonic-task-manager/issues)

---

<p align="center">
  <em>Remember: Productivity should feel like a gentle river, not a raging waterfall 🌊</em>
</p>

<p align="center">
  Made with ❤️ by Sonic Team
</p>
