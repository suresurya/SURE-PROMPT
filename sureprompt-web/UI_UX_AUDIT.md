# SurePrompt Web UI/UX Audit and Redesign Notes

## Scope
- Stack retained: Spring Boot + Thymeleaf + Material Web + existing controller routes and API contracts.
- Pages audited: shared layout, feed, login, post prompt, profile, prompt detail, settings, admin dashboard/users/reports.
- Additional route coverage added: explore, saved, profile-edit templates that were referenced by controllers but missing.

## Key Findings (Before Redesign)

### Critical
1. Route-template mismatch caused runtime failures.
- Controllers returned templates that did not exist: `explore`, `saved`, `profile-edit`.
- Impact: direct navigation to these routes fails at runtime.

2. Prompt detail had non-functional actions.
- Template called `toggleLike`, `toggleSave`, and `addComment` handlers that were not implemented in shipped JS.
- Impact: core engagement workflow appeared interactive but failed during use.

### High
3. Visual system fragmentation.
- Multiple pages relied on large inline style blocks with inconsistent spacing, shape, typography, and hierarchy.
- Result: inconsistent experience and high cost to maintain.

4. Duplicated and diverging feed logic.
- Feed behavior was split across scripts with conflicting assumptions about response shape and page wiring.
- Result: difficult debugging and fragile future changes.

5. Mixed component semantics and interaction cues.
- Similar actions used different visual patterns per page.
- Result: reduced scanability and lower confidence in primary actions.

### Medium
6. Overly heavy dark treatment with low differentiation.
- Palette and emphasis levels did not separate primary/secondary information clearly.
- Result: reduced readability and weak information hierarchy.

7. Notification and account surfaces lacked coherent visual grouping.
- Dropdown, avatar, and nav actions looked assembled rather than system-driven.

8. Admin pages lacked clear workflow rhythm.
- Moderation actions, notes, and state transitions were present but visually noisy.

## Redesign Principles Applied
- Built one cohesive design system in `static/css/main.css` using tokenized color, typography, spacing, and component states.
- Moved from inconsistent page-local styles to shared primitives and semantic sections.
- Preserved all existing JS IDs/classes that power behavior (notifications, like/save, admin dialogs, feed tabs).
- Introduced a lighter editorial visual language for hierarchy clarity, readability, and reduced cognitive load.
- Added responsive behavior for all key surfaces and action bars.

## Implemented Structural Improvements

### Shared shell
- Refactored shared layout fragments for a consistent application frame:
  - `layout/base.html`
  - `layout/main.html`
  - `layout/navbar.html`
  - `layout/footer.html`
- Preserved required behavior hooks:
  - Notifications: `notificationBtn`, `notificationDropdown`, `notificationBadge`, `notifList`, `markAllReadBtn`
  - Account menu: `avatarMenuBtn`, `avatarMenu`, `logoutMenuItem`, `logoutForm`

### Design system
- Replaced global stylesheet with a unified token system and reusable component classes:
  - `static/css/main.css`
- Consolidated card, form, table, admin, profile, and detail page patterns.
- Kept CSS variables used by existing scripts (`--text-muted`, `--accent-color`, `--danger-color`, etc.).

### Functional JS alignment
- Rebuilt `static/js/prompt.js` as a single interaction layer for:
  - Feed loading/tabbing/infinite scroll
  - Like/save toggles
  - Prompt copy action
  - Comment submission on detail page
- Added missing global handlers required by template calls (`toggleLike`, `toggleSave`, `addComment`).

## Page-by-Page Outcomes
- Feed (`index.html`): clear hierarchy, unified card system, intact tab + infinite interactions.
- Login (`login.html`): streamlined auth card and cleaner action grouping.
- Post Prompt (`post-prompt.html`): structured editor sections with stable metadata/tag flow.
- Profile (`profile.html`): improved hero/stats readability and consistent prompt list styling.
- Prompt Detail (`prompt-detail.html`): fixed interaction gaps and improved content framing.
- Settings (`settings.html`): clarified API key state and preference rows.
- Admin (`admin/dashboard.html`, `admin/users.html`, `admin/reports.html`): cleaner moderation workflow and table/report readability.

## Missing Template Gaps Closed
New templates created to satisfy existing controller returns:
- `templates/explore.html`
- `templates/saved.html`
- `templates/profile-edit.html`

## Residual Risks
- Existing secondary CSS files (`feed.css`, `profile.css`, `prompt.css`, `form.css`) remain in repository but are no longer required by redesigned templates.
- Explore route filtering UX currently uses client-side API fetch composition; further server-side query hydration can be added for shareable deep links with all filters.

## Recommended Follow-up
1. Remove or archive now-unused legacy CSS files to prevent future style drift.
2. Add smoke tests for template route availability (`/explore`, `/saved`, `/profile/edit`, `/prompts/{id}`).
3. Add integration tests for prompt detail interactions (like/save/comment).
