# Sonic Task Manager - Project Specification

## Core Philosophy
Create a personal task manager that feels like a "personal assistant" rather than a "business tool", helping users feel **manageable** and **motivated** instead of stressed and overwhelmed.

## Key Principles

### 1. Single Focus Approach
- **Primary**: Always highlight 1 main task user should work on now
- **Secondary**: Other tasks "wait gently", don't compete for attention
- **Mental model**: "Today, just focus on this one thing"

### 2. Anti-Stress Design
- Human language: "Quick task", "Needs focus" instead of "2 hours", "High priority"
- Gentle encouragement: "Any progress is good progress"
- No perfectionist pressure: "Just rough sketch needed, no need to be perfect"
- Organic visual layout: Not rigid grid, has personality

### 3. Progressive Disclosure
- Default view: Minimal info, only "what's next"
- On-demand detail: Click to expand subtasks, progress
- Context-aware: Show relevant info based on time/situation

## Core Features

### 1. Task Management with 4 Types

#### **Deadline Tasks** (Projects with deadlines)
- **Example**: Photovista project, Buy laptop
- **Features**:
  - Progress tracking with subtasks
  - Deadline countdown (gentle, not stressful)
  - Visual priority based on urgency
  - Clear "next action"

#### **Habit/Learning Tasks** (Long-term, no deadline)
- **Example**: Piano practice, Learn guitar, Read books
- **Features**:
  - Journey progress: "Currently at lesson 5/20", "Chapter 3"
  - No pressure reminders: "Whenever you want"
  - Gentle encouragement when inactive

#### **Reminder Tasks** (Gentle nudges)
- **Example**: Think about buying car, Find new apartment
- **Features**:
  - Surface occasionally, not daily
  - "Gentle reminder" vibe
  - Easy snooze: "Maybe next week"

#### **Event/Time-sensitive** (Fixed time events)
- **Example**: Meetings, Birthdays, Memorial services
- **Features**:
  - Auto-appear when relevant (today, tomorrow)
  - Auto-dismiss after time passes
  - Calendar integration

### 2. Smart Priority System

#### **2-Dimensional Classification:**
- **Priority** (Urgency level): High/Medium/Low
- **Complexity** (Difficulty): Easy/Medium/Hard

#### **4 Combinations with different treatment:**
```
High Priority + Easy    → "Quick wins today"
High Priority + Complex → "Main focus today"
Low Priority + Easy     → "Filler tasks"
Low Priority + Complex  → "Background projects"
```

### 3. Workspace Metaphor Interface

#### **Main Focus Area** (Center stage)
- 1 task only, large and prominent
- Context info: "Just need to do X, no need for Y"
- Primary action button: "Continue", "Start"

#### **"Next Up" Stack** (Side area)
- Stack of tasks like papers on desk
- Organic rotation, overlapping
- Click to move to main focus

#### **Sticky Note Reminders** (Scattered)
- Gentle reminders, not urgent
- Visual like real sticky notes
- Hover to see details

#### **Completed Area** (Slide off)
- Completed tasks automatically slide off screen
- Subtle celebration animation

### 4. Natural Language Interface

#### **Complexity Descriptions:**
- "Quick task" (5-30 minutes)
- "Work gradually" (can break into parts)
- "Needs focus" (need dedicated time block)
- "Bit complex" (requires research)

#### **Priority Language:**
- "Need to do soon" instead of "High Priority"
- "Important but flexible" instead of "Medium Priority"
- "Someday/maybe" instead of "Low Priority"

### 5. Smart Suggestions & Context Awareness

#### **Today's Focus Logic:**
```
IF (deadline today OR overdue) → Urgent section
ELSE IF (high priority + user energy match) → Main focus
ELSE IF (high priority + easy) → Quick wins
ELSE → Background/someday
```

#### **Gentle Nudging:**
- "Haven't practiced piano in a while?" (no pressure)
- "Light day today, maybe do some quick tasks"
- "Photovista deadline approaching, want to start?"

### 6. Mood & Energy Awareness

#### **Daily Mood Indicator:**
- "Chill day", "Busy day", "Focus day"
- Auto-suggest tasks matching mood
- Adjust expectations accordingly

#### **Energy-based Task Suggestion:**
- Morning: Complex tasks
- Afternoon: Meetings/communication
- Evening: Quick wins, planning

## Technical Features

### 1. Task Creation & Management
- **Quick Add**: Minimal form, smart categorization
- **Smart Defaults**: Auto-categorize based on keywords
- **Bulk Import**: From other tools, calendar, email

### 2. Progress Tracking
- **Visual Progress**: Organic progress indicators, not rigid bars
- **Milestone Celebration**: Subtle animations when complete
- **Gentle Analytics**: "Completed 8 tasks this week" (positive tone)

### 3. Calendar Integration
- **Auto-import Events**: Meetings, deadlines from Google Calendar
- **Smart Scheduling**: Suggest time slots for tasks
- **Context Awareness**: Know when user is busy or free

### 4. Data Persistence
- **Local-first**: SQLite database, no cloud dependency
- **Easy Backup**: Export/import JSON/CSV
- **Sync Option**: Optional cloud sync for future

## User Experience Flow

### Morning Routine
1. Open app → See mood indicator + today's gentle agenda
2. See 1 main focus task with encouraging context
3. Quick scan "next up" stack and reminders
4. Choose focus task → Other tasks dim away

### During Day
1. Work on focus task → gentle progress updates
2. Complete task → subtle celebration → next task surfaces
3. Add urgent tasks → smart categorization
4. Check reminders → easy snooze/reschedule

### Evening Review
1. See accomplished tasks slide away
2. Gentle prep for tomorrow
3. Adjust tomorrow's focus if needed
4. Feel good about progress made

## Success Metrics
- **Emotional**: User feels calm, motivated
- **Behavioral**: Daily consistent usage without burnout
- **Functional**: Complete important tasks without stress
- **Retention**: Long-term usage (don't abandon after few weeks)

## Technical Architecture

### Backend (Spring Boot)
- **Package**: `com.sonic.taskmanager`
- **Database**: SQLite local storage
- **API**: RESTful endpoints
- **Business Logic**: All priority calculation, focus determination, reminder logic

### Frontend (React)
- **Presentation**: Pure UI rendering based on backend data
- **Interactions**: User actions, animations, layout
- **Responsive**: Works on desktop and mobile browsers

### Deployment
- **Development**: Separate backend (8080) and frontend (5173) servers
- **Production**: Single JAR file with embedded frontend
- **Future**: Electron wrapper for desktop app

---

*Goal: Create a tool users want to open daily because it's helpful and comforting, not out of obligation.*