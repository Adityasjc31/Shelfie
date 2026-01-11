# 🔀 Topic 8: Git Commands & Concepts - Interview Questions & Answers

This document contains comprehensive interview questions and answers about Git version control system, including commands, workflows, and best practices.

---

## Q1: What is Git? Explain the difference between Git and GitHub.

**Answer:**

**Git** is a distributed version control system (DVCS) for tracking changes in source code during software development.

**GitHub** is a web-based hosting platform for Git repositories that adds collaboration features.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       GIT vs GITHUB                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   GIT (Version Control System):                                          │
│   ─────────────────────────────                                          │
│   • Software installed on your computer                                  │
│   • Tracks changes in files                                             │
│   • Works offline                                                       │
│   • Command-line tool                                                   │
│   • Created by Linus Torvalds                                           │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   GITHUB (Hosting Platform):                                             │
│   ─────────────────────────────                                          │
│   • Web-based platform (github.com)                                     │
│   • Hosts Git repositories online                                       │
│   • Adds collaboration features:                                        │
│     - Pull Requests                                                     │
│     - Issues tracking                                                   │
│     - Code reviews                                                      │
│     - CI/CD (GitHub Actions)                                            │
│     - Project boards                                                    │
│   • Owned by Microsoft                                                  │
│                                                                          │
│   Alternatives: GitLab, Bitbucket, Azure DevOps                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Key Git Concepts:

| Concept | Description |
|---------|-------------|
| **Repository** | Container for your project (folder with .git) |
| **Commit** | Snapshot of changes at a point in time |
| **Branch** | Independent line of development |
| **Remote** | Server hosting the repository (e.g., GitHub) |
| **Clone** | Copy of a remote repository |
| **Push** | Send commits to remote |
| **Pull** | Get commits from remote |
| **Merge** | Combine branches |

### Distributed vs Centralized:

```
┌─────────────────────────────────────────────────────────────────────────┐
│   CENTRALIZED (SVN)                 DISTRIBUTED (Git)                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│        ┌───────────┐                    ┌───────────┐                   │
│        │  Server   │                    │  Remote   │                   │
│        │   Repo    │                    │   Repo    │                   │
│        └─────┬─────┘                    └─────┬─────┘                   │
│              │                                │                          │
│     ┌────────┼────────┐            ┌─────────┼─────────┐               │
│     │        │        │            │         │         │               │
│     ▼        ▼        ▼            ▼         ▼         ▼               │
│   ┌───┐    ┌───┐    ┌───┐      ┌───────┐ ┌───────┐ ┌───────┐          │
│   │Dev│    │Dev│    │Dev│      │  Dev  │ │  Dev  │ │  Dev  │          │
│   │ 1 │    │ 2 │    │ 3 │      │ +Repo │ │ +Repo │ │ +Repo │          │
│   └───┘    └───┘    └───┘      └───────┘ └───────┘ └───────┘          │
│                                                                          │
│   • Devs don't have full      • Each dev has FULL copy                  │
│     history                   • Can work offline                        │
│   • Need server access        • Better performance                       │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q2: Explain the Git workflow and staging area.

**Answer:**

Git has three main areas where your files can exist:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       GIT THREE AREAS                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Working Directory         Staging Area           Repository            │
│   (Your Files)              (Index)                (.git folder)         │
│                                                                          │
│   ┌─────────────────┐      ┌─────────────────┐    ┌─────────────────┐   │
│   │                 │      │                 │    │                 │   │
│   │  file1.java     │      │  file1.java     │    │    Commit A     │   │
│   │  file2.java (M) │ ───▶ │  file2.java     │ ──▶│    Commit B     │   │
│   │  file3.java (?) │ add  │                 │ commit │  Commit C  │   │
│   │                 │      │                 │    │                 │   │
│   └─────────────────┘      └─────────────────┘    └─────────────────┘   │
│                                                                          │
│   Status: Modified (M)     Status: Staged         Status: Committed     │
│           Untracked (?)                                                  │
│                                                                          │
│   COMMANDS:                                                              │
│   ─────────                                                              │
│   git add file.java        # Stage file                                 │
│   git add .                # Stage all changes                          │
│   git commit -m "message"  # Commit staged files                        │
│   git status               # See what's staged/modified                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Basic Workflow:

```bash
# 1. Create/modify files in working directory
echo "Hello" > hello.txt

# 2. Check status
git status
# Output: Untracked files: hello.txt

# 3. Stage changes (add to staging area)
git add hello.txt
# OR stage all
git add .

# 4. Check status again
git status
# Output: Changes to be committed: new file: hello.txt

# 5. Commit to repository
git commit -m "Add hello.txt file"

# 6. Push to remote
git push origin main
```

### Common Status Indicators:

| Status | Meaning |
|--------|---------|
| **Untracked** | New file, not tracked by Git |
| **Modified** | Tracked file changed |
| **Staged** | Changes ready to commit |
| **Committed** | Changes saved to repository |

---

## Q3: What are the most important Git commands?

**Answer:**

### Essential Commands:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       ESSENTIAL GIT COMMANDS                             │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SETUP & CONFIGURATION:                                                 │
│   ──────────────────────                                                 │
│   git init                     # Initialize new repository              │
│   git clone <url>              # Clone remote repository                │
│   git config --global user.name "Your Name"                             │
│   git config --global user.email "email@example.com"                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BASIC WORKFLOW:                                                        │
│   ───────────────                                                        │
│   git status                   # Check status                           │
│   git add <file>               # Stage file                             │
│   git add .                    # Stage all changes                      │
│   git commit -m "message"      # Commit with message                    │
│   git push                     # Push to remote                         │
│   git pull                     # Pull from remote                       │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BRANCHING:                                                             │
│   ──────────                                                             │
│   git branch                   # List branches                          │
│   git branch <name>            # Create branch                          │
│   git checkout <branch>        # Switch to branch                       │
│   git checkout -b <branch>     # Create AND switch to branch            │
│   git switch <branch>          # Switch to branch (newer command)       │
│   git switch -c <branch>       # Create AND switch (newer)              │
│   git merge <branch>           # Merge branch into current              │
│   git branch -d <branch>       # Delete branch                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   VIEWING HISTORY:                                                       │
│   ────────────────                                                       │
│   git log                      # View commit history                    │
│   git log --oneline            # Compact view                           │
│   git log --graph              # Branch visualization                   │
│   git diff                     # Show unstaged changes                  │
│   git diff --staged            # Show staged changes                    │
│   git show <commit>            # Show commit details                    │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   UNDOING CHANGES:                                                       │
│   ────────────────                                                       │
│   git restore <file>           # Discard working directory changes      │
│   git restore --staged <file>  # Unstage file                           │
│   git reset HEAD~1             # Undo last commit (keep changes)        │
│   git reset --hard HEAD~1      # Undo last commit (discard changes)     │
│   git revert <commit>          # Create new commit that undoes changes  │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   REMOTE OPERATIONS:                                                     │
│   ──────────────────                                                     │
│   git remote -v                # List remotes                           │
│   git remote add origin <url>  # Add remote                             │
│   git fetch                    # Download from remote (no merge)        │
│   git push -u origin main      # Push and set upstream                  │
│   git pull origin main         # Fetch + merge from remote              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   STASHING:                                                              │
│   ─────────                                                              │
│   git stash                    # Save changes temporarily               │
│   git stash list               # List stashes                           │
│   git stash pop                # Apply and remove latest stash          │
│   git stash apply              # Apply without removing                 │
│   git stash drop               # Delete latest stash                    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Q4: Explain the difference between merge and rebase.

**Answer:**

Both integrate changes from one branch into another, but in different ways.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       MERGE vs REBASE                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   STARTING POINT:                                                        │
│                                                                          │
│   main:     A ─── B ─── C                                               │
│                    \                                                     │
│   feature:          D ─── E                                             │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   MERGE (git checkout main && git merge feature):                        │
│                                                                          │
│   main:     A ─── B ─── C ─────── M (merge commit)                      │
│                    \             /                                       │
│   feature:          D ─── E ────                                        │
│                                                                          │
│   ✅ Preserves complete history                                          │
│   ✅ Non-destructive                                                     │
│   ✅ Easy to understand merge points                                    │
│   ❌ Can create "messy" history with many merge commits                  │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   REBASE (git checkout feature && git rebase main):                      │
│                                                                          │
│   main:     A ─── B ─── C                                               │
│                          \                                               │
│   feature:                D' ─── E' (commits replayed on top of C)      │
│                                                                          │
│   Then fast-forward merge:                                               │
│   main:     A ─── B ─── C ─── D' ─── E'                                 │
│                                                                          │
│   ✅ Clean, linear history                                               │
│   ✅ No merge commits                                                    │
│   ❌ Rewrites history (changes commit hashes)                            │
│   ⚠️ Never rebase shared/pushed branches!                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# MERGE
git checkout main
git merge feature-branch
# Creates merge commit, preserves history

# REBASE
git checkout feature-branch
git rebase main
git checkout main
git merge feature-branch  # Fast-forward merge
# Linear history, cleaner

# Interactive rebase (squash commits)
git rebase -i HEAD~3  # Rebase last 3 commits
# Opens editor to squash, reorder, edit commits
```

### When to Use:

| Scenario | Recommendation |
|----------|----------------|
| Feature branch → main | Merge (or squash merge) |
| Update feature with main changes | Rebase preferred |
| Shared/pushed branches | Merge only (never rebase) |
| Clean up local commits | Interactive rebase |
| Collaborative branches | Merge |

---

## Q5: What is a Git conflict? How do you resolve it?

**Answer:**

A **conflict** occurs when Git cannot automatically merge changes because the same lines were modified differently in two branches.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       GIT CONFLICT                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SCENARIO:                                                              │
│                                                                          │
│   Branch main:     Changed line 10 to "Hello World"                     │
│   Branch feature:  Changed line 10 to "Hi There"                        │
│                                                                          │
│   When merging: CONFLICT! Which change to keep?                         │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   CONFLICT MARKERS IN FILE:                                              │
│                                                                          │
│   <<<<<<< HEAD                                                          │
│   Hello World                                                           │
│   =======                                                               │
│   Hi There                                                              │
│   >>>>>>> feature-branch                                                │
│                                                                          │
│   • <<<<<<< HEAD: Your current branch's version                         │
│   • =======: Separator                                                  │
│   • >>>>>>> feature-branch: Incoming branch's version                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Resolution Steps:

```bash
# 1. Attempt merge (conflict occurs)
git merge feature-branch
# CONFLICT (content): Merge conflict in file.txt

# 2. Check which files have conflicts
git status
# Both modified: file.txt

# 3. Open file and manually resolve
# Edit the file, remove conflict markers, keep desired changes
# Before:
<<<<<<< HEAD
Hello World
=======
Hi There
>>>>>>> feature-branch

# After (your resolution):
Hello There World  # Or whatever you decide

# 4. Mark as resolved by staging
git add file.txt

# 5. Complete the merge
git commit -m "Merge feature-branch, resolved conflicts"

# OR abort the merge
git merge --abort
```

### Resolution Strategies:

| Strategy | Command/Action |
|----------|---------------|
| Keep current (ours) | `git checkout --ours file.txt` |
| Keep incoming (theirs) | `git checkout --theirs file.txt` |
| Manual edit | Edit file, remove markers |
| Visual tool | `git mergetool` |
| Abort merge | `git merge --abort` |

### VS Code Conflict Resolution:
VS Code provides clickable options above conflict markers:
- Accept Current Change
- Accept Incoming Change
- Accept Both Changes
- Compare Changes

---

## Q6: What is cherry-pick? When would you use it?

**Answer:**

**Cherry-pick** copies a specific commit from one branch to another without merging the entire branch.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       CHERRY-PICK                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   BEFORE:                                                                │
│                                                                          │
│   main:     A ─── B ─── C                                               │
│                    \                                                     │
│   feature:          D ─── E ─── F (want only F on main)                 │
│                                                                          │
│   AFTER: git checkout main && git cherry-pick F                          │
│                                                                          │
│   main:     A ─── B ─── C ─── F'  (F copied as new commit)              │
│                    \                                                     │
│   feature:          D ─── E ─── F                                       │
│                                                                          │
│   Note: F' has same changes but different commit hash                   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# Cherry-pick single commit
git checkout main
git cherry-pick abc123  # commit hash

# Cherry-pick multiple commits
git cherry-pick abc123 def456

# Cherry-pick range (exclusive of first)
git cherry-pick abc123..xyz789

# Cherry-pick without committing (stage only)
git cherry-pick --no-commit abc123

# If conflicts occur
git cherry-pick --continue  # after resolving
git cherry-pick --abort     # to cancel
```

### Use Cases:

| Scenario | Example |
|----------|---------|
| **Hotfix** | Apply bug fix from release branch to main |
| **Backporting** | Apply feature to older version |
| **Partial merge** | Only want specific commits |
| **Rescue work** | Recover commits from deleted branch |

### Example: Hotfix Workflow

```bash
# Bug fixed on release-1.0 branch, need on main too
git log release-1.0  # Find the fix commit: abc123

git checkout main
git cherry-pick abc123
git push origin main

# Now both branches have the fix
```

---

## Q7: What is git stash? When would you use it?

**Answer:**

**Git stash** temporarily saves uncommitted changes so you can work on something else, then restore them later.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       GIT STASH                                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   SCENARIO:                                                              │
│   You're working on a feature, but need to urgently fix a bug           │
│                                                                          │
│   Working Directory:                                                     │
│   ┌─────────────────────────────────────────┐                           │
│   │ file1.java (modified)                   │                           │
│   │ file2.java (modified)                   │                           │
│   │ file3.java (new, untracked)             │                           │
│   └─────────────────────────────────────────┘                           │
│                     │                                                    │
│                     │ git stash                                         │
│                     ▼                                                    │
│   ┌─────────────────────────────────────────┐                           │
│   │           STASH STACK                   │                           │
│   │  ┌────────────────────────────────────┐ │                           │
│   │  │ stash@{0}: WIP on feature: abc123  │ │                           │
│   │  │ file1.java, file2.java             │ │                           │
│   │  └────────────────────────────────────┘ │                           │
│   └─────────────────────────────────────────┘                           │
│                                                                          │
│   Working Directory: CLEAN (can switch branches safely)                 │
│                                                                          │
│   Later: git stash pop → Restores changes                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# Save current changes to stash
git stash
# OR with message
git stash save "Work in progress on login feature"

# Include untracked files
git stash -u
# OR
git stash --include-untracked

# List all stashes
git stash list
# stash@{0}: WIP on feature: abc123 message
# stash@{1}: WIP on main: def456 earlier work

# Apply latest stash (keep in stash)
git stash apply

# Apply and remove from stash
git stash pop

# Apply specific stash
git stash apply stash@{1}

# View stash contents
git stash show stash@{0}
git stash show -p stash@{0}  # With diff

# Delete stashes
git stash drop            # Drop latest
git stash drop stash@{1}  # Drop specific
git stash clear           # Delete all stashes

# Create branch from stash
git stash branch new-branch stash@{0}
```

### Use Cases:

| Scenario | Command |
|----------|---------|
| Switch branch with uncommitted changes | `git stash` → switch → `git stash pop` |
| Pull with local changes | `git stash` → `git pull` → `git stash pop` |
| Quick context switch | Stash current work |
| Test something on clean state | Stash → test → pop |

---

## Q8: What is git reset vs git revert?

**Answer:**

Both undo changes, but in fundamentally different ways:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       RESET vs REVERT                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Current state:  A ─── B ─── C ─── D (HEAD)                            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   GIT RESET (Rewrite history - DANGEROUS on shared branches):           │
│   ─────────────────────────────────────────────────────────             │
│                                                                          │
│   git reset --soft HEAD~2                                               │
│   Result: A ─── B (HEAD)                                                │
│           C, D changes are STAGED                                        │
│                                                                          │
│   git reset --mixed HEAD~2  (default)                                   │
│   Result: A ─── B (HEAD)                                                │
│           C, D changes are UNSTAGED (in working directory)              │
│                                                                          │
│   git reset --hard HEAD~2                                               │
│   Result: A ─── B (HEAD)                                                │
│           C, D changes are DELETED ⚠️                                   │
│                                                                          │
│   ⚠️ History is rewritten - commits C, D are gone!                      │
│   ⚠️ Never use on pushed/shared branches!                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   GIT REVERT (Safe - Creates new commit):                                │
│   ───────────────────────────────────────                                │
│                                                                          │
│   git revert D (or git revert HEAD)                                     │
│   Result: A ─── B ─── C ─── D ─── D' (HEAD)                             │
│           D' is a NEW commit that UNDOES D's changes                    │
│                                                                          │
│   git revert C (revert specific commit)                                 │
│   Result: A ─── B ─── C ─── D ─── C' (HEAD)                             │
│           C' undoes C's changes                                          │
│                                                                          │
│   ✅ History is preserved                                                │
│   ✅ Safe for shared branches                                            │
│   ✅ Can revert any commit, not just recent                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Reset Modes:

| Mode | Staging Area | Working Directory | Use Case |
|------|--------------|-------------------|----------|
| `--soft` | Keeps changes | Keeps changes | Redo commit message |
| `--mixed` | Clears | Keeps changes | Redo what to commit |
| `--hard` | Clears | Clears | Discard everything |

### When to Use:

| Scenario | Command |
|----------|---------|
| Undo local commits | `git reset HEAD~1` |
| Undo pushed commits | `git revert <commit>` |
| Completely discard work | `git reset --hard HEAD` |
| Unstage files | `git reset HEAD file.txt` |
| Undo specific old commit | `git revert <commit>` |

---

## Q9: Explain Git branching strategies (GitFlow, GitHub Flow).

**Answer:**

### GitFlow:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           GITFLOW                                        │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   main        ●─────────────────●──────────────────●──────────────●     │
│               (v1.0)            (v1.1)             (v2.0)                │
│                     ╲         ╱      ╲           ╱                      │
│   hotfix                    ●──●                                        │
│                             (bugfix)                                     │
│                                                                          │
│   release         ●────●────●          ●────●────●                      │
│                   (testing)            (testing)                         │
│                  ╱          ╲        ╱          ╲                       │
│   develop   ●───●────●────●──●──●───●────●────●──●──●───●               │
│                    ╱ ╱ ╲╲            ╱ ╱ ╲╲                             │
│   feature    ●───●   ●──●     ●───●   ●──●                              │
│              (login)  (cart)  (search) (checkout)                       │
│                                                                          │
│   BRANCHES:                                                              │
│   ─────────                                                              │
│   main      - Production-ready code only                                │
│   develop   - Integration branch for features                           │
│   feature/* - New features (branch from develop)                        │
│   release/* - Prepare for release (testing, bugfixes)                   │
│   hotfix/*  - Emergency production fixes                                │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### GitHub Flow (Simpler):

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         GITHUB FLOW                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   main       ●──────●───────●───────●───────●───────●                   │
│                    ╱       ╱       ╱       ╱                            │
│   feature    ●───●    ●──●    ●──●    ●──●                              │
│              (PR)     (PR)    (PR)    (PR)                              │
│                                                                          │
│   WORKFLOW:                                                              │
│   ─────────                                                              │
│   1. Create branch from main                                            │
│   2. Make changes, commit                                               │
│   3. Open Pull Request                                                  │
│   4. Review and discuss                                                 │
│   5. Deploy for testing (optional)                                      │
│   6. Merge to main                                                      │
│   7. Deploy to production                                               │
│                                                                          │
│   RULES:                                                                 │
│   ──────                                                                 │
│   • main is always deployable                                           │
│   • Branch for any change                                               │
│   • Pull Request for review                                             │
│   • Merge after approval                                                │
│   • Deploy immediately after merge                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Comparison:

| Aspect | GitFlow | GitHub Flow |
|--------|---------|-------------|
| **Complexity** | Complex | Simple |
| **Branches** | main, develop, feature, release, hotfix | main, feature |
| **Best for** | Scheduled releases | Continuous deployment |
| **Team size** | Large teams | Small to medium |
| **Release cycle** | Planned releases | Deploy anytime |

---

## Q10: What are some Git best practices?

**Answer:**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       GIT BEST PRACTICES                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   COMMIT MESSAGES:                                                       │
│   ────────────────                                                       │
│   ✅ Good:                                                               │
│   "Add user authentication with JWT"                                    │
│   "Fix null pointer in OrderService.processOrder()"                     │
│   "Refactor database connection pooling for better performance"         │
│                                                                          │
│   ❌ Bad:                                                                │
│   "fix"                                                                  │
│   "updates"                                                              │
│   "WIP"                                                                  │
│   "asdfasdf"                                                            │
│                                                                          │
│   Format:                                                                │
│   <type>: <subject>                                                     │
│   feat: Add login functionality                                         │
│   fix: Resolve memory leak in image processing                          │
│   docs: Update API documentation                                        │
│   refactor: Simplify user validation logic                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   BRANCHING:                                                             │
│   ──────────                                                             │
│   ✅ Use descriptive branch names                                        │
│      feature/user-authentication                                        │
│      bugfix/login-redirect-loop                                         │
│      hotfix/security-vulnerability                                      │
│                                                                          │
│   ✅ Keep branches short-lived                                           │
│   ✅ Delete merged branches                                              │
│   ✅ Pull from main frequently                                           │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COMMITS:                                                               │
│   ────────                                                               │
│   ✅ Commit often (small, focused commits)                               │
│   ✅ One logical change per commit                                       │
│   ✅ Test before committing                                              │
│   ❌ Never commit sensitive data (passwords, keys)                       │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   COLLABORATION:                                                         │
│   ──────────────                                                         │
│   ✅ Always pull before push                                             │
│   ✅ Use Pull Requests for code review                                   │
│   ✅ Never force push to shared branches                                 │
│   ✅ Keep main/master protected                                          │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   .GITIGNORE:                                                            │
│   ───────────                                                            │
│   Always include:                                                        │
│   • IDE files (.idea/, .vscode/)                                        │
│   • Build outputs (target/, build/, node_modules/)                      │
│   • Environment files (.env, *.local)                                   │
│   • OS files (.DS_Store, Thumbs.db)                                     │
│   • Logs (*.log)                                                        │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common .gitignore:

```gitignore
# IDE
.idea/
.vscode/
*.iml

# Build
target/
build/
out/
*.class
*.jar

# Dependencies
node_modules/

# Environment
.env
.env.local
*.local

# Logs
*.log
logs/

# OS
.DS_Store
Thumbs.db
```

---

## Q11: What is the difference between `git fetch` and `git pull`? (Beginner)

**Answer:**

Both commands retrieve data from a remote repository, but they behave differently:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       FETCH vs PULL                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   GIT FETCH:                                                             │
│   ──────────                                                             │
│   • Downloads commits, files, and refs from remote                      │
│   • Does NOT merge changes into your working branch                     │
│   • Safe operation - just updates remote-tracking branches              │
│                                                                          │
│   Remote:    A ─── B ─── C ─── D                                        │
│   Local:     A ─── B                                                    │
│   After fetch:                                                           │
│   Local:     A ─── B           (your branch unchanged)                  │
│   origin/main: A ─── B ─── C ─── D (remote-tracking updated)            │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   GIT PULL:                                                              │
│   ──────────                                                             │
│   • git pull = git fetch + git merge                                    │
│   • Downloads AND merges remote changes into current branch             │
│   • May cause merge conflicts                                           │
│                                                                          │
│   Remote:    A ─── B ─── C ─── D                                        │
│   Local:     A ─── B                                                    │
│   After pull:                                                            │
│   Local:     A ─── B ─── C ─── D (your branch updated)                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# Fetch only (safe, review before merging)
git fetch origin
git log HEAD..origin/main  # See what's new
git merge origin/main      # Merge when ready

# Pull (fetch + merge in one step)
git pull origin main

# Pull with rebase instead of merge
git pull --rebase origin main
```

### When to Use:

| Scenario | Recommendation |
|----------|----------------|
| Want to review changes first | `git fetch` |
| Quick update of local branch | `git pull` |
| Avoid merge commits | `git pull --rebase` |
| CI/CD pipelines | `git fetch` (more control) |

---

## Q12: What is HEAD in Git? (Beginner)

**Answer:**

**HEAD** is a pointer that refers to the current location in your repository - typically the latest commit on your current branch.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           HEAD POINTER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Normal State (HEAD points to branch):                                  │
│                                                                          │
│   main:    A ─── B ─── C ─── D                                          │
│                              ▲                                           │
│                              │                                           │
│                            HEAD (via main)                               │
│                                                                          │
│   .git/HEAD contains: ref: refs/heads/main                              │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   After switching branch (git checkout feature):                         │
│                                                                          │
│   main:    A ─── B ─── C ─── D                                          │
│                  │                                                       │
│   feature:       └─── E ─── F                                           │
│                             ▲                                            │
│                             │                                            │
│                           HEAD (via feature)                             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common HEAD References:

| Reference | Meaning |
|-----------|---------|
| `HEAD` | Current commit |
| `HEAD~1` or `HEAD~` | One commit before HEAD |
| `HEAD~2` | Two commits before HEAD |
| `HEAD^` | Parent of HEAD (same as HEAD~1) |
| `HEAD^2` | Second parent (for merge commits) |

### Commands Using HEAD:

```bash
# View current HEAD
git show HEAD
cat .git/HEAD

# Reference previous commits
git show HEAD~1        # Previous commit
git diff HEAD~3 HEAD   # Compare last 3 commits

# Reset using HEAD
git reset HEAD~1       # Undo last commit

# Create tag at HEAD
git tag v1.0 HEAD
```

---

## Q13: What is a detached HEAD state and how do you fix it? (Intermediate)

**Answer:**

**Detached HEAD** occurs when HEAD points directly to a commit instead of a branch. Any commits made in this state may be lost if you switch branches.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                       DETACHED HEAD STATE                                │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Normal State:                                                          │
│                                                                          │
│   main:    A ─── B ─── C ─── D                                          │
│                              ▲                                           │
│                            HEAD → main → D                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   After: git checkout abc123 (checkout specific commit)                  │
│                                                                          │
│   main:    A ─── B ─── C ─── D                                          │
│                  ▲                                                       │
│                HEAD (detached, pointing directly to B)                   │
│                                                                          │
│   ⚠️ Warning: You are in 'detached HEAD' state...                        │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   If you make commits in detached state:                                 │
│                                                                          │
│   main:    A ─── B ─── C ─── D                                          │
│                  │                                                       │
│   (orphan):      └─── X ─── Y                                           │
│                             ▲                                            │
│                           HEAD                                           │
│                                                                          │
│   X, Y are "orphan" commits - may be garbage collected!                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### How It Happens:

```bash
# Checkout specific commit
git checkout abc123

# Checkout tag
git checkout v1.0

# Checkout remote branch directly
git checkout origin/main
```

### How to Fix:

```bash
# Option 1: Go back to a branch (discard detached commits)
git checkout main

# Option 2: Create a new branch to save your work
git checkout -b my-new-branch
# OR
git switch -c my-new-branch

# Option 3: If you already switched away, recover orphan commits
git reflog                    # Find the commit hash
git checkout abc123           # Go back to orphan commit
git checkout -b recovered-work
```

---

## Q14: What is `git bisect` and how do you use it? (Intermediate)

**Answer:**

**Git bisect** uses binary search to find the commit that introduced a bug. It's incredibly efficient for large histories.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           GIT BISECT                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   You have 100 commits, something broke. Which commit caused it?        │
│                                                                          │
│   Commits: 1 ─ 2 ─ 3 ─ ... ─ 50 ─ ... ─ 100                            │
│            ✅                  ❓          ❌                            │
│          (known good)                   (known bad)                      │
│                                                                          │
│   Binary search: Check 50, then 25 or 75, etc.                          │
│   Instead of 100 checks → only log₂(100) ≈ 7 checks needed!             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### How to Use:

```bash
# 1. Start bisect
git bisect start

# 2. Mark current commit as bad (has bug)
git bisect bad

# 3. Mark a known good commit (no bug)
git bisect good abc123

# Git checks out middle commit, you test it
# 4. Mark each commit Git shows you
git bisect good   # If this commit doesn't have the bug
# OR
git bisect bad    # If this commit has the bug

# Git narrows down and eventually shows:
# "abc123 is the first bad commit"

# 5. Exit bisect mode
git bisect reset
```

### Automated Bisect:

```bash
# Use a test script to automate
git bisect start HEAD abc123
git bisect run ./test.sh

# test.sh should exit 0 for good, non-0 for bad
# Git runs it automatically until it finds the bad commit
```

### Example:

```bash
# Bug introduced between v1.0 and current
git bisect start
git bisect bad HEAD
git bisect good v1.0

# Git: "Bisecting: 25 revisions left to test"
# Git checks out commit in the middle

# Run your test, does bug exist?
./run-tests.sh
# Bug exists here
git bisect bad

# Git checks out earlier commit
# Bug doesn't exist here
git bisect good

# Continue until Git finds the exact commit
git bisect reset  # Return to original state
```

---

## Q15: What is `git reflog` and when would you use it? (Intermediate)

**Answer:**

**Git reflog** (reference log) records all changes to HEAD and branch tips in your local repository. It's your safety net for recovering lost commits.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           GIT REFLOG                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Every time HEAD changes, reflog records it:                           │
│                                                                          │
│   $ git reflog                                                          │
│                                                                          │
│   abc123 HEAD@{0}: commit: Add new feature                              │
│   def456 HEAD@{1}: checkout: moving from main to feature                │
│   ghi789 HEAD@{2}: commit: Fix bug                                      │
│   jkl012 HEAD@{3}: reset: moving to HEAD~2                              │
│   mno345 HEAD@{4}: commit: Initial commit                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   Even "lost" commits appear in reflog!                                 │
│                                                                          │
│   Scenario: Accidentally did git reset --hard HEAD~5                    │
│                                                                          │
│   Before: A ─── B ─── C ─── D ─── E (HEAD)                              │
│   After:  A (HEAD)  [B, C, D, E appear "lost" but are in reflog]        │
│                                                                          │
│   Recovery: git reflog → find E's hash → git reset --hard E             │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# View reflog
git reflog

# View reflog for specific branch
git reflog show feature-branch

# View with dates
git reflog --date=relative

# Reference using reflog
git show HEAD@{2}           # HEAD 2 moves ago
git show main@{1.week.ago}  # main 1 week ago
```

### Recovery Scenarios:

```bash
# Recover from accidental reset --hard
git reflog
# Find the commit hash before the reset
git reset --hard abc123

# Recover deleted branch
git reflog
git checkout -b recovered-branch abc123

# Recover after bad rebase
git reflog
git reset --hard HEAD@{5}  # Go back 5 HEAD changes
```

### Important Notes:

- Reflog is **local only** - not shared with remote
- Entries expire after 90 days (configurable)
- Essential tool for recovery from mistakes

---

## Q16: What are Git hooks? Give examples. (Intermediate)

**Answer:**

**Git hooks** are scripts that Git executes automatically before or after events like commit, push, and merge. They enable automation and enforcement of policies.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                           GIT HOOKS                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Location: .git/hooks/                                                  │
│                                                                          │
│   CLIENT-SIDE HOOKS:                                                     │
│   ────────────────────                                                   │
│   pre-commit       - Before commit (validate code, run linters)         │
│   prepare-commit-msg - Modify default commit message                    │
│   commit-msg       - Validate commit message format                     │
│   post-commit      - After commit (notifications)                       │
│   pre-push         - Before push (run tests)                            │
│   post-checkout    - After checkout (update dependencies)               │
│   post-merge       - After merge (update dependencies)                  │
│                                                                          │
│   SERVER-SIDE HOOKS:                                                     │
│   ────────────────────                                                   │
│   pre-receive      - Before accepting push                              │
│   update           - Before updating each branch                        │
│   post-receive     - After push (trigger CI/CD)                         │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Example: Pre-commit Hook (Run Tests)

```bash
#!/bin/bash
# .git/hooks/pre-commit

echo "Running tests before commit..."
npm test

if [ $? -ne 0 ]; then
    echo "Tests failed! Commit aborted."
    exit 1
fi

echo "Tests passed!"
exit 0
```

### Example: Commit-msg Hook (Validate Message Format)

```bash
#!/bin/bash
# .git/hooks/commit-msg

commit_regex='^(feat|fix|docs|style|refactor|test|chore): .{10,}$'

if ! grep -qE "$commit_regex" "$1"; then
    echo "Invalid commit message format!"
    echo "Expected: <type>: <description (min 10 chars)>"
    echo "Types: feat, fix, docs, style, refactor, test, chore"
    exit 1
fi

exit 0
```

### Setting Up Hooks:

```bash
# Create hook file
touch .git/hooks/pre-commit

# Make executable (Unix)
chmod +x .git/hooks/pre-commit

# Edit with your script
vim .git/hooks/pre-commit

# To skip hooks temporarily
git commit --no-verify
git push --no-verify
```

### Sharing Hooks (Using Husky for Node.js):

```bash
# Install Husky
npm install husky --save-dev

# Enable Git hooks
npx husky install

# Add pre-commit hook
npx husky add .git/hooks/pre-commit "npm test"
```

---

## Q17: What are Git submodules? When would you use them? (Advanced)

**Answer:**

**Git submodules** allow you to include one Git repository inside another as a subdirectory, while keeping them separate.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         GIT SUBMODULES                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Parent Repository (my-app)                                             │
│   ├── src/                                                              │
│   ├── tests/                                                            │
│   ├── libs/                                                             │
│   │   └── shared-library/  ← Submodule (separate Git repo)             │
│   ├── .gitmodules          ← Submodule configuration                   │
│   └── .git/                                                             │
│                                                                          │
│   .gitmodules contains:                                                  │
│   [submodule "libs/shared-library"]                                     │
│       path = libs/shared-library                                        │
│       url = https://github.com/company/shared-library.git               │
│                                                                          │
│   Parent repo stores: commit hash of submodule (not contents)           │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# Add a submodule
git submodule add https://github.com/user/repo.git libs/repo

# Clone repository with submodules
git clone --recurse-submodules https://github.com/user/main-repo.git
# OR after cloning
git submodule init
git submodule update

# Update all submodules to latest commit
git submodule update --remote

# Update specific submodule
git submodule update --remote libs/shared-library

# Check submodule status
git submodule status

# Remove submodule
git submodule deinit libs/shared-library
git rm libs/shared-library
rm -rf .git/modules/libs/shared-library
```

### Use Cases:

| Scenario | Example |
|----------|---------|
| Shared libraries | Common utilities across projects |
| Third-party code | Vendoring dependencies |
| Microservices | Shared contracts/schemas |
| Documentation | Separate doc repository |

### Pros and Cons:

| Pros | Cons |
|------|------|
| Clear dependency versions | Complex workflow |
| Separate history | Extra commands to remember |
| Independent development | Can confuse team members |
| Version pinning | Submodule update can break things |

---

## Q18: What are Git worktrees? (Advanced)

**Answer:**

**Git worktrees** allow you to have multiple working directories attached to the same repository, each checked out to a different branch.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         GIT WORKTREES                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Without worktrees:                                                     │
│   my-project/                                                           │
│   ├── src/                                                              │
│   └── .git/                                                             │
│   (can only work on ONE branch at a time)                               │
│                                                                          │
│   ─────────────────────────────────────────────────────────────────────  │
│                                                                          │
│   With worktrees:                                                        │
│                                                                          │
│   my-project/          ← main branch                                    │
│   ├── src/                                                              │
│   └── .git/                                                             │
│                                                                          │
│   ../my-project-feature/  ← feature branch (linked worktree)           │
│   ├── src/                                                              │
│   └── .git (file pointing to main repo)                                 │
│                                                                          │
│   ../my-project-hotfix/   ← hotfix branch (linked worktree)            │
│   ├── src/                                                              │
│   └── .git (file pointing to main repo)                                 │
│                                                                          │
│   All share the same Git repository but have separate working dirs!    │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Commands:

```bash
# Create worktree for existing branch
git worktree add ../my-project-feature feature-branch

# Create worktree with new branch
git worktree add -b new-feature ../my-project-new-feature main

# List all worktrees
git worktree list

# Remove worktree
git worktree remove ../my-project-feature
# OR delete directory and prune
rm -rf ../my-project-feature
git worktree prune
```

### Use Cases:

| Scenario | Benefit |
|----------|---------|
| Reviewing PR while working | Keep your work, check out PR in new worktree |
| Running long tests | Run tests in one worktree, work in another |
| Comparing branches | Side-by-side comparison |
| Hotfix during development | Fix production without stashing |
| Building multiple branches | Build different versions simultaneously |

### Worktree vs Clone:

| Aspect | Worktree | Clone |
|--------|----------|-------|
| Repository | Shared | Separate copy |
| Disk space | Minimal | Full repository |
| Fetch updates | Once for all | Each clone separately |
| Branch locking | Yes (can't checkout same branch) | No |

---

## Q19: Explain interactive rebase and its uses. (Advanced)

**Answer:**

**Interactive rebase** (`git rebase -i`) allows you to modify, combine, reorder, or delete commits before sharing them.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     INTERACTIVE REBASE                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Before:  A ─── B ─── C ─── D ─── E (HEAD)                             │
│                  └───────────────────┘                                   │
│                  commits to rebase (B, C, D, E)                         │
│                                                                          │
│   git rebase -i HEAD~4 (or git rebase -i A)                             │
│                                                                          │
│   Editor opens:                                                          │
│   ┌──────────────────────────────────────────────────────────────┐      │
│   │ pick abc123 Commit B message                                  │      │
│   │ pick def456 Commit C message                                  │      │
│   │ pick ghi789 Commit D message                                  │      │
│   │ pick jkl012 Commit E message                                  │      │
│   │                                                                │      │
│   │ # Commands:                                                    │      │
│   │ # p, pick   = use commit                                       │      │
│   │ # r, reword = use commit, but edit message                     │      │
│   │ # e, edit   = use commit, but stop for amending                │      │
│   │ # s, squash = use commit, meld into previous                   │      │
│   │ # f, fixup  = like squash, but discard message                 │      │
│   │ # d, drop   = remove commit                                    │      │
│   │ # (delete line = drop commit)                                  │      │
│   └──────────────────────────────────────────────────────────────┘      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Common Operations:

**1. Squash Multiple Commits:**

```bash
git rebase -i HEAD~3

# Change file to:
pick abc123 Feature implementation
squash def456 Fix typo
squash ghi789 Add tests

# Result: Three commits become one
```

**2. Reword Commit Message:**

```bash
git rebase -i HEAD~2

# Change 'pick' to 'reword':
reword abc123 Old message
pick def456 Another commit

# Editor opens to change message
```

**3. Reorder Commits:**

```bash
git rebase -i HEAD~3

# Just reorder the lines:
pick ghi789 Third commit (now first)
pick abc123 First commit (now second)
pick def456 Second commit (now third)
```

**4. Split a Commit:**

```bash
git rebase -i HEAD~2

# Change to 'edit':
edit abc123 Big commit to split
pick def456 Another commit

# Git stops at that commit
git reset HEAD~1          # Unstage changes
git add file1.java
git commit -m "Part 1"
git add file2.java
git commit -m "Part 2"
git rebase --continue
```

### ⚠️ Warning:

- **Never rebase pushed/shared commits!**
- Rewriting history breaks collaboration
- Use only for local, unpushed commits

---

## Q20: Explain Git internals: blobs, trees, and commits. (Advanced)

**Answer:**

Understanding Git's internal object model helps you understand how Git works under the hood.

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        GIT OBJECTS                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Git stores everything as objects, identified by SHA-1 hash            │
│                                                                          │
│   BLOB (Binary Large Object):                                           │
│   ────────────────────────────                                           │
│   • Stores file CONTENTS only (not name or permissions)                 │
│   • Each unique file content = one blob                                 │
│                                                                          │
│   TREE:                                                                  │
│   ─────                                                                  │
│   • Like a directory                                                    │
│   • Contains pointers to blobs (files) and other trees (subdirs)        │
│   • Stores filenames, permissions, and references                       │
│                                                                          │
│   COMMIT:                                                                │
│   ───────                                                                │
│   • Points to a tree (snapshot of entire project)                       │
│   • Contains metadata: author, committer, message, parent(s)            │
│   • Links to parent commit(s) forming history chain                     │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Object Structure:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     COMMIT STRUCTURE                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   commit 5a3c...                                                        │
│   ├── tree 8b2f... ─────────────────┐                                   │
│   ├── parent 3d1a...                │                                   │
│   ├── author: John <john@...>       │                                   │
│   ├── committer: John <john@...>    │                                   │
│   └── message: "Add feature"        │                                   │
│                                      │                                   │
│          ┌───────────────────────────┘                                   │
│          ▼                                                               │
│   tree 8b2f...                                                          │
│   ├── blob abc1... README.md                                            │
│   ├── blob def2... pom.xml                                              │
│   └── tree ghi3... src/ ──────────┐                                     │
│                                    │                                     │
│          ┌─────────────────────────┘                                     │
│          ▼                                                               │
│   tree ghi3... (src)                                                    │
│   ├── tree jkl4... main/                                                │
│   └── tree mno5... test/                                                │
│                                                                          │
│          (and so on...)                                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### Exploring Internals:

```bash
# View object type
git cat-file -t abc123

# View object content
git cat-file -p abc123

# Example: View commit object
git cat-file -p HEAD
# tree 8b2f...
# parent 3d1a...
# author John <john@email.com> 1609459200 +0000
# committer John <john@email.com> 1609459200 +0000
#
# Add new feature

# View tree object
git cat-file -p HEAD^{tree}
# 100644 blob abc123    README.md
# 100644 blob def456    pom.xml
# 040000 tree ghi789    src

# View blob content
git cat-file -p abc123
# (file contents)

# List all objects
git rev-list --objects --all

# Where objects are stored
ls .git/objects/
# Objects stored in folders by first 2 chars of hash
```

### Key Insights:

| Concept | Insight |
|---------|---------|
| **Immutability** | Objects never change once created |
| **Deduplication** | Same content = same hash = stored once |
| **Efficiency** | Only changed files get new blobs |
| **Integrity** | Hash verifies content hasn't changed |
| **Branches** | Just pointers to commit hashes |
| **Tags** | Also just pointers (lightweight) or objects (annotated) |

---

## Summary

| Concept | Key Point |
|---------|-----------|
| **Git vs GitHub** | Tool vs hosting platform |
| **Three areas** | Working directory → Staging → Repository |
| **Merge vs Rebase** | Preserve history vs linear history |
| **Conflicts** | Manual resolution of same-line changes |
| **Cherry-pick** | Copy specific commit to another branch |
| **Stash** | Temporarily save uncommitted changes |
| **Reset** | Rewrite history (dangerous on shared) |
| **Revert** | Safe undo with new commit |
| **GitFlow** | Complex, scheduled releases |
| **GitHub Flow** | Simple, continuous deployment |

---

> **Next Topic:** Spring Security
