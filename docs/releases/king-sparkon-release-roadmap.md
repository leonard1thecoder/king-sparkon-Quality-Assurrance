# King Sparkon Tracker Release Roadmap

## Product vision

King Sparkon Tracker should become a professional barcode-first tracking platform for owners, workers, and companies. The product vision is to support reliable barcode scanning, inventory visibility, worker tips, secure payments, reporting, auditability, and eventually Android field operations.

## Release planning principles

1. Every feature must have an automation-analysis table before development starts.
2. Backend APIs must have REST Assured regression coverage.
3. High-value web flows must have Selenium E2E coverage.
4. Android flows must have Appium coverage when the mobile app exists.
5. Risky payment, WhatsApp, email, and security flows must include manual review and sandbox validation.
6. Performance-critical barcode flows must have JMeter coverage.
7. Release readiness requires passing tests, reviewed reports, and documented manual exceptions.

## Release 0.1 - QA Foundation

**Goal:** Establish the framework and project structure for professional QA execution.

| Epic | Feature | Outcome | Test owner | Automation layer |
| --- | --- | --- | --- | --- |
| QA Framework | MVC QA framework | Model/View/Controller test architecture exists | QA/Engineering | Core |
| QA Framework | HTML report | Test results display pass/fail/duration/coverage | QA | Core |
| QA Framework | Selenium screenshots | Web screenshots captured and optionally uploaded | QA | Selenium |
| QA Framework | Appium screenshots | Android screenshots captured when app/device is available | QA | Appium |
| QA Framework | Supabase upload | Screenshots pushed to Supabase Storage when enabled | QA/DevOps | Core |
| QA Framework | Automation analysis template | Features can be planned before implementation | QA/Product | Documentation |

## Release 0.2 - Core Backend Readiness

**Goal:** Prove the backend can support the core tracking domain.

| Epic | Feature | Outcome | Test owner | Automation layer |
| --- | --- | --- | --- | --- |
| Backend API | Health and OpenAPI readiness | Backend exposes operational endpoints | Backend QA | REST Assured |
| Auth | Login/session API | Users can authenticate securely | Backend QA | REST Assured + Manual security |
| Barcode | Barcode scan API | Valid barcode scan records are processed | Backend QA | REST Assured + JMeter |
| Inventory | Product and inventory API | Owner sees accurate stock state | Backend QA | REST Assured |
| Reporting | Dashboard summary API | Owner receives dashboard metrics | Backend QA | REST Assured |

## Release 0.3 - Professional Web E2E

**Goal:** Prove public and authenticated website flows.

| Epic | Feature | Outcome | Test owner | Automation layer |
| --- | --- | --- | --- | --- |
| Landing | Home page | Professional landing page loads with required content | Web QA | Selenium |
| Auth | Login page | Inputs, placeholders, links, and SEO basics render | Web QA | Selenium |
| Auth | Register page | Inputs, placeholders, links, and SEO basics render | Web QA | Selenium |
| Footer | Social links | Footer/social links route correctly | Web QA | Selenium |
| Scanner | Barcode scanner page | Scanner UI renders and screenshot evidence is captured | Web QA | Selenium + Screenshot |

## Release 0.4 - Tips and Payment Readiness

**Goal:** Prepare worker tips and owner payment workflows safely.

| Epic | Feature | Outcome | Test owner | Automation layer |
| --- | --- | --- | --- | --- |
| Tips | Worker QR payment page | Client can open worker tip page | QA | Selenium |
| Tips | Tip API creation | Tip record is created with amount/status | Backend QA | REST Assured |
| Tips | Owner marks tip paid | Owner can update tip status to paid | Backend QA | REST Assured |
| Payments | Stripe sandbox payment link | Payment link is created only in sandbox | QA/Owner | REST Assured + Manual |
| Messaging | WhatsApp/email referral link | Link is sent through configured provider | QA/Owner | Manual + Contract tests |

## Release 0.5 - Android Field App

**Goal:** Prepare Android automation for barcode scanner development.

| Epic | Feature | Outcome | Test owner | Automation layer |
| --- | --- | --- | --- | --- |
| Android | App launch | Android app starts on emulator/device | Mobile QA | Appium |
| Android | Barcode input | Worker can input/scan barcode | Mobile QA | Appium |
| Android | Scan result | Worker sees result/confirmation | Mobile QA | Appium |
| Android | Offline/error states | Worker sees useful error state | Mobile QA | Appium + Manual |
| Android | Screenshot evidence | Screenshots are uploaded to Supabase | Mobile QA | Appium + Supabase |

## Release 1.0 - Production Candidate

**Goal:** Ship a stable production-ready King Sparkon Tracker MVP.

| Gate | Required evidence |
| --- | --- |
| API regression | REST Assured report passes critical P0/P1 tests |
| Web E2E | Selenium report passes landing/auth/scanner/footer flows |
| Android readiness | Appium launch/screenshot flows pass when Android app is ready |
| Security | ZAP baseline reviewed and no high-risk release blockers remain |
| Performance | JMeter barcode/API baseline meets agreed performance gates |
| Manual validation | Payment, WhatsApp, email, and sensitive workflows manually signed off |
| Screenshot evidence | Key Selenium/Appium screenshots stored locally or uploaded to Supabase |

## Backlog workflow

1. Create feature backlog item.
2. Fill automation-analysis table.
3. Decide automatable/manual/hybrid/not-ready.
4. Add or update `.svc` test case data.
5. Add model/view/controller test implementation.
6. Add end-to-end steps in the View override method.
7. Run automation locally or in GitHub Actions.
8. Review HTML report and screenshots.
9. Move feature to release candidate only when evidence is clean.
