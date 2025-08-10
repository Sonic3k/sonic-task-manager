# Sonic Task Manager - Project Structure

## Overall Architecture
```
sonic-task-manager/
├── sonic-task-manager-api/     # Spring Boot Backend
├── sonic-task-manager-web/     # React Frontend  
├── README.md
└── docker-compose.yml          # Optional: for easy setup
```

## Architecture Philosophy: Backend-Heavy, Frontend-Light

### Backend Responsibilities (Business Logic):
```java
// WorkspaceService.java - Main business logic
@Service
public class WorkspaceService {
    
    // Calculate which task should be focus
    public Task calculateFocusTask(List<Task> tasks) {
        // Priority + deadline + complexity logic here
    }
    
    // Decide which reminders to show today  
    public List<Task> getActiveReminders() {
        // Random selection, frequency logic, snooze logic
    }
    
    // Calculate next up stack order
    public List<Task> getNextUpStack() {
        // Sorting, prioritization, max items logic
    }
    
    // Determine daily mood/status
    public String calculateDailyMood(List<Task> tasks) {
        // "Chill day", "Busy day" based on workload
    }
    
    // Smart task suggestions
    public List<Task> getQuickWinSuggestions() {
        // High priority + easy complexity logic
    }
}
```

### Frontend Responsibilities (Presentation):
```jsx
// Frontend focuses on rendering pre-calculated data
function Workspace() {
    const { workspace } = useWorkspace(); // Fetch data from backend
    
    return (
        <div className="workspace">
            {/* Render based on backend response */}
            <FocusArea task={workspace.focusTask} />
            <NextUpStack tasks={workspace.nextUpStack} />
            <Reminders items={workspace.activeReminders} />
            
            {/* Show/hide based on backend flags */}
            {workspace.showQuickWins && (
                <QuickWins tasks={workspace.quickWins} />
            )}
            
            {/* Style based on backend mood calculation */}
            <MoodIndicator mood={workspace.dailyMood} />
        </div>
    );
}
```

## Backend Structure (Spring Boot)

```
sonic-task-manager-api/
├── pom.xml
├── src/
│   └── main/
│       ├── java/com/sonic/taskmanager/
│       │   ├── SonicTaskManagerApplication.java
│       │   ├── config/
│       │   │   ├── DatabaseConfig.java  # SQLite specific configuration
│       │   │   └── WebConfig.java       # CORS configuration for React dev
│       │   ├── controller/
│       │   │   ├── TaskController.java
│       │   │   ├── WorkspaceController.java
│       │   │   └── PreferencesController.java
│       │   ├── service/ (Business Logic Layer)
│       │   │   ├── TaskService.java           # CRUD + auto-categorization
│       │   │   ├── WorkspaceService.java      # Main business logic - calculates focus task, priorities  
│       │   │   ├── FocusCalculatorService.java # Determines focus task based on business rules
│       │   │   ├── ReminderService.java       # Decides which reminders to show and frequency
│       │   │   └── MoodCalculatorService.java # Calculates daily mood based on workload
│       │   ├── repository/
│       │   │   ├── TaskRepository.java
│       │   │   ├── HabitSessionRepository.java
│       │   │   └── PreferencesRepository.java
│       │   ├── model/
│       │   │   ├── Task.java
│       │   │   ├── HabitSession.java
│       │   │   ├── Preferences.java
│       │   │   └── dto/
│       │   │       ├── WorkspaceDto.java
│       │   │       ├── TaskDto.java
│       │   │       └── CreateTaskDto.java
│       │   └── util/
│       │       ├── FocusCalculator.java   # Business rules
│       │       └── DateUtils.java
│       └── resources/
│           ├── application.properties   # Main configuration
│           ├── data.sql                # Initial sample data (optional)
│           └── schema.sql              # Database schema (optional - JPA auto-creates)
└── target/
    └── sonic-task-manager-api.jar
```

## Frontend Structure (React + Vite)

```
sonic-task-manager-web/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.jsx                    # App entry point
│   ├── App.jsx                     # Main app component
│   ├── components/ (Presentation Layer)
│   │   ├── workspace/
│   │   │   ├── Workspace.jsx       # Renders workspace response from backend
│   │   │   ├── FocusArea.jsx       # Shows focusTask from backend
│   │   │   ├── NextUpStack.jsx     # Renders backend-sorted stack
│   │   │   ├── StickyReminders.jsx # Frontend handles positioning  
│   │   │   └── CompletedArea.jsx   # Shows based on backend flags
│   │   ├── tasks/
│   │   │   ├── TaskCard.jsx        # Renders task properties
│   │   │   ├── TaskForm.jsx        # Sends data to backend for processing
│   │   │   ├── ProgressBar.jsx     # Shows backend-calculated progress
│   │   │   └── SubtaskList.jsx     # Displays backend-provided subtasks
│   │   ├── ui/
│   │   │   ├── Button.jsx          # Reusable button
│   │   │   ├── Modal.jsx           # Modal dialog
│   │   │   └── LoadingSpinner.jsx
│   │   └── layout/
│   │       ├── Header.jsx          # Date/mood indicator
│   │       └── QuickAdd.jsx        # Quick task creation
│   ├── services/
│   │   ├── api.js                  # Axios setup + base config
│   │   ├── taskService.js          # Task CRUD operations
│   │   ├── workspaceService.js     # Workspace data fetching
│   │   └── calendarService.js      # Future calendar integration
│   ├── hooks/
│   │   ├── useTasks.js             # Task state management
│   │   ├── useWorkspace.js         # Workspace state
│   │   └── useLocalStorage.js      # Local preferences
│   ├── utils/
│   │   ├── dateUtils.js            # Date formatting
│   │   ├── taskUtils.js            # Task calculation helpers
│   │   └── constants.js            # App constants
│   ├── styles/
│   │   ├── index.css               # Global styles
│   │   ├── workspace.css           # Workspace-specific styles
│   │   └── components/             # Component-specific styles
│   │       ├── TaskCard.module.css
│   │       ├── FocusArea.module.css
│   │       └── StickyNote.module.css
│   └── assets/
│       └── images/                 # Icons, illustrations
└── dist/                           # Built files for production
```

## Key Configuration Files

### Backend: `application.properties`
```properties
spring.application.name=sonic-task-manager-api

# SQLite Database Configuration
spring.datasource.url=jdbc:sqlite:${user.home}/.sonic-task-manager/database.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.username=
spring.datasource.password=

# JPA/Hibernate Configuration
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

# Server Configuration
server.port=8080

# CORS for React development
cors.allowed-origins=http://localhost:5173
```

### Backend: `pom.xml` (Key Dependencies)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.12</version>
        <relativePath/>
    </parent>
    
    <groupId>com.sonic</groupId>
    <artifactId>taskmanager-api</artifactId>
    <version>1.0.0</version>
    <name>Sonic Task Manager API</name>
    <description>Personal task manager with workspace metaphor</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        
        <!-- SQLite Database -->
        <dependency>
            <groupId>org.xerial</groupId>
            <artifactId>sqlite-jdbc</artifactId>
            <version>3.45.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-community-dialects</artifactId>
        </dependency>
        
        <!-- Development Tools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        
        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Backend: `WebConfig.java` (CORS Configuration)
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins}")
    private String allowedOrigins;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

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

### Frontend: `vite.config.js`
```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

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

### Frontend: `package.json`
```json
{
  "name": "sonic-task-manager-web",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "axios": "^1.6.0"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.0",
    "vite": "^5.0.0"
  }
}
```

## API Endpoints Structure

### Key Endpoint: GET /api/workspace (Contains calculated business data)
```json
{
  "dailyMood": "chill",           // Backend calculated based on workload
  "focusTask": {
    "id": 1,
    "title": "Photovista wireframe", 
    "focusContext": "Just rough sketch, no need perfect",
    "isUrgent": false,            // Backend determined urgency
    "complexityLabel": "Needs focus",
    "priority": "high",
    "deadline": "2025-08-30",
    "daysUntilDeadline": 22
  },
  "nextUpStack": [                // Backend sorted by priority + urgency
    {
      "id": 2,
      "title": "Reply VIP email",
      "priority": "high",
      "complexity": "easy",
      "urgencyLevel": "high",
      "estimatedMinutes": 15
    }
  ],
  "activeReminders": [            // Backend selected which reminders to show
    {
      "id": 3,
      "title": "Think about buying car",
      "reminderType": "gentle",
      "lastShown": "2025-08-05",
      "frequency": "weekly"
    }
  ],
  "quickWins": [                  // Backend identified easy + important tasks
    {
      "id": 4,
      "title": "Backup files",
      "priority": "medium",
      "complexity": "easy",
      "estimatedMinutes": 5
    }
  ],
  "showSections": {               // Backend decides what sections to display
    "urgentTasks": false,
    "quickWins": true,
    "habits": false,
    "reminders": true
  },
  "workloadAssessment": {         // Backend calculated workload
    "totalTasks": 8,
    "urgentCount": 1, 
    "estimatedHours": 4.5,
    "recommendation": "Light day, good for quick tasks"
  }
}
```

### Other Simple Endpoints:
```
POST /api/tasks                  # Create task (backend auto-categorizes)
PUT  /api/tasks/{id}/complete    # Complete task (backend recalculates workspace)  
PUT  /api/tasks/{id}/snooze      # Snooze reminder (backend sets next show time)
POST /api/workspace/refresh      # Recalculate entire workspace
```

## Architecture Implementation Examples

### Frontend Implementation:
```jsx
// Frontend handles presentation and user interactions
function Workspace() {
    const { workspace } = useWorkspace(); // Fetch business data from backend
    
    return (
        <div className="workspace">
            {/* Backend determined focus task, frontend handles positioning */}
            <FocusArea 
                task={workspace.focusTask} 
                className="main-focus-center" 
            />
            
            {/* Backend sorted tasks, frontend handles stack layout */}
            <NextUpStack 
                tasks={workspace.nextUpStack}
                className="stack-right-side"
            />
            
            {/* Backend selected reminders, frontend positions sticky notes */}
            {workspace.showSections.reminders && (
                <StickyReminders 
                    reminders={workspace.activeReminders}
                    // Frontend calculates positions based on count
                />
            )}
            
            {/* Frontend handles all CSS positioning and animations */}
            <QuickWins tasks={workspace.quickWins} />
        </div>
    );
}

// Frontend component handles its own positioning logic
function StickyReminders({ reminders }) {
    const positions = generateStickyPositions(reminders.length); // Frontend logic
    
    return (
        <>
            {reminders.map((reminder, index) => (
                <StickyNote 
                    key={reminder.id}
                    style={{
                        position: 'absolute',
                        top: positions[index].top,      // Frontend calculated
                        left: positions[index].left,    // Frontend calculated  
                        transform: `rotate(${positions[index].rotation})` // Frontend calculated
                    }}
                    content={reminder.title}
                />
            ))}
        </>
    );
}
```

### Backend Service Implementation:
```java
@Service
public class WorkspaceService {
    
    public WorkspaceDto calculateTodaysWorkspace() {
        List<Task> allTasks = taskRepository.findActive();
        
        // Backend handles business logic only
        Task focusTask = focusCalculator.determineBestFocus(allTasks);
        List<Task> nextUp = priorityCalculator.sortNextUpStack(allTasks, focusTask);
        List<Reminder> reminders = reminderService.selectTodaysReminders();
        String mood = moodCalculator.assessDailyWorkload(allTasks);
        List<Task> quickWins = taskService.findQuickWins(allTasks);
        
        // Determine what sections to show based on data
        ShowSections showSections = ShowSections.builder()
            .reminders(!reminders.isEmpty())
            .quickWins(!quickWins.isEmpty())
            .urgentTasks(hasUrgentTasks(allTasks))
            .build();
        
        return WorkspaceDto.builder()
            .focusTask(focusTask)
            .nextUpStack(nextUp) 
            .activeReminders(reminders)
            .quickWins(quickWins)
            .dailyMood(mood)
            .showSections(showSections)
            .workloadAssessment(calculateWorkload(allTasks))
            .build();
    }
    
    private WorkloadAssessment calculateWorkload(List<Task> tasks) {
        int totalTasks = tasks.size();
        int urgentCount = (int) tasks.stream().filter(Task::isUrgent).count();
        double estimatedHours = tasks.stream()
            .mapToDouble(Task::getEstimatedHours)
            .sum();
            
        return WorkloadAssessment.builder()
            .totalTasks(totalTasks)
            .urgentCount(urgentCount)
            .estimatedHours(estimatedHours)
            .recommendation(generateRecommendation(urgentCount, estimatedHours))
            .build();
    }
}
```

## Development Workflow

### Day-to-day Development:
```bash
# Terminal 1: Backend
cd sonic-task-manager-api
./mvnw spring-boot:run

# Terminal 2: Frontend  
cd sonic-task-manager-web
npm run dev

# Access app at http://localhost:5173
# API calls proxy to http://localhost:8080
```

### Building for Production:
```bash
# Build frontend
cd sonic-task-manager-web
npm run build

# Copy built files to Spring Boot static resources
cp -r dist/* ../sonic-task-manager-api/src/main/resources/static/

# Build backend with embedded frontend
cd sonic-task-manager-api
./mvnw clean package

# Result: Single JAR with both frontend and backend
java -jar target/sonic-task-manager-api.jar
```

## Future Electron Packaging:
```
sonic-task-manager-electron/
├── main.js              # Electron main process
├── package.json         # Electron dependencies
├── sonic-task-manager-api.jar     # Your Spring Boot app
└── dist/                # Electron build output
```

## Getting Started:

```bash
# 1. Create backend using Spring Initializr
spring init --build=maven --java-version=17 \
  --boot-version=3.3.12 \
  --dependencies=web,data-jpa,validation,devtools \
  --name=SonicTaskManagerApi \
  --package-name=com.sonic.taskmanager \
  --group-id=com.sonic \
  --artifact-id=taskmanager-api \
  sonic-task-manager-api

# 2. Add SQLite dependencies manually to pom.xml
# (sqlite-jdbc and hibernate-community-dialects)

# 3. Create frontend  
npm create react@latest sonic-task-manager-web
cd sonic-task-manager-web
npm install axios

# 4. Start development
# Terminal 1: ./mvnw spring-boot:run (backend)
# Terminal 2: npm run dev (frontend)
```

## Key Benefits of This Structure:

1. **Clear Separation**: Backend/frontend completely separate during development
2. **Easy Development**: Hot reload for both React and Spring Boot  
3. **Simple Deployment**: Single JAR for distribution (sonic-task-manager-api.jar)
4. **Scalable**: Easy to add new features/components
5. **Professional**: Industry-standard structure for both technologies
6. **Flexible Naming**: Clear distinction between API and Web components