# CAM2COOK Android design

## Target device

Design for Samsung Galaxy S-series Android phones in portrait orientation. Use a 360 × 800 dp reference canvas for layout checks, but implement using responsive Compose layouts so screens adapt to other Galaxy sizes and font scales.

## Source of truth

- Product UX and screen content: [CAM2COOK source design in Figma](https://www.figma.com/design/QjeivGZjLYvgU9kVV34FvK/Cam2Cook2?node-id=121-173)
- This app is implemented in Kotlin with Jetpack Compose and Material 3. Figma screen structure should be expressed with native Android components rather than exported iOS chrome.

## Visual tokens

| Token | Value | Use |
| --- | --- | --- |
| Canvas | `#F2F2F7` | App background |
| Surface | `#FFFFFF` | Cards and input surfaces |
| Primary | `#0B6B4F` | Main actions and selected navigation |
| Accent | `#18A874` | Positive/fresh states |
| Mint | `#D1F2DE` | Illustration and food tiles |
| Pale green | `#E8F7EB` | Supporting illustration surfaces |
| Text | `#0B1410` | Primary text |
| Secondary text | `#5A6861` | Supporting copy |
| Border | `#E3E3E8` | Light dividers and outlines |
| At-risk | `#B4233A` | Expired / review required |
| Caution | `#966400` | Caution text |

## Platform conventions

- Respect Android status and navigation bar insets. Use light system icons against the light canvas.
- Do not draw an iPhone Dynamic Island, iOS status-bar mock, or iOS home indicator.
- Use Material input, button, focus, keyboard and back-navigation behavior.
- Keep controls comfortably tappable; avoid relying on exact iOS absolute positioning.
- Allow font scaling and vertical scrolling on smaller devices.
- Dates inferred by vision should be presented as estimates, not food-safety guarantees.

## Screen structure

- Welcome: fridge and produce illustration, “Less waste, more flavour” chip, short value proposition, Get started and Sign in.
- Home: greeting/profile, green scan hero, “Use these first” priority food rows, smart recipe suggestion, bottom navigation.
- Fridge/inventory: search, freshness filters, editable quantities, mark-as-used.
- Scan/review: capture entry point, sample recognition preview, editable detected items, save to inventory.
- Recipes: search/filter, ingredient context and Cook now.
- Cook Mode: current step, progress, timer and completion actions.
- You/settings: profile, safety notes and sign out.

## Scope note

CameraX capture, live AI, API-backed auth, remote recipe generation and durable persistence are separate integration tasks; the current slice retains clearly labeled demo behavior.
