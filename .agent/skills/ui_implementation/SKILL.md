---
name: UI Implementation
description: Enforce usage of the project's design system (core/theme) and reusable components (core/components) when implementing any UI layouts.
---

# UI Implementation Skill

This skill ensures consistent UI implementation by mandating the use of the project's established design system and component library.

## When This Skill Applies

Automatically apply this skill when:
- Creating new screen layouts
- Adding or modifying UI components
- Working with colors, typography, or spacing
- User requests involving: "layout", "screen", "component", "button", "input", "form", "UI", "design"

---

## Theme System Rules

### Colors

**ALWAYS use theme helper functions from `core.theme.Color`:**

```kotlin
// ✅ CORRECT
import core.theme.*

Text(color = getPrimaryColor())
Box(modifier = Modifier.background(getSurfaceColor()))
Icon(tint = getOnBackgroundColor())

// ❌ WRONG - Hardcoded colors
Text(color = Color(0xFF138AFC))
Box(modifier = Modifier.background(Color.White))

// ❌ WRONG - Direct MaterialTheme access
Text(color = MaterialTheme.colorScheme.primary)
```

### Spacing

**ALWAYS use `Spacing` or `Dimens` objects from `core.theme.Dimens`:**

```kotlin
// ✅ CORRECT
import core.theme.*

Modifier.padding(Spacing.Medium)           // 16dp
Modifier.padding(horizontal = Dimens.L)    // 24dp
Spacer(modifier = Modifier.height(Spacing.Small))  // 12dp
Modifier.size(ComponentDimens.IconSizeMedium)      // 24dp

// ❌ WRONG - Hardcoded dp values
Modifier.padding(16.dp)
Spacer(modifier = Modifier.height(12.dp))
```

### Typography

**ALWAYS use typography helper functions from `core.theme.Typography`:**

```kotlin
// ✅ CORRECT
import core.theme.*

Text(text = "Title", style = getTextTitleLarge())
Text(text = "Body", style = getTextBodyMedium())
Text(text = "Label", style = getTextLabelSmall())

// ❌ WRONG - Hardcoded text styles
Text(text = "Title", fontSize = 22.sp, fontWeight = FontWeight.Bold)

// ❌ WRONG - Direct MaterialTheme access
Text(text = "Title", style = MaterialTheme.typography.titleLarge)
```

---

## Component Library Rules

**ALWAYS check `core/components/` before creating new UI elements.**

### Available Components

#### Buttons (`CoreButton.kt`)
| Component | Use For |
|-----------|---------|
| `CoreButton` | Primary action button |
| `CoreTonalButton` | Secondary filled button |
| `CoreOutlinedButton` | Medium emphasis action |
| `CoreTextButton` | Low emphasis action |
| `CoreIconButton` | Icon-only button |
| `CoreFilledIconButton` | Icon button with filled background |
| `CoreTonalIconButton` | Icon button with tonal background |
| `CoreOutlinedIconButton` | Icon button with border |
| `CoreSmallFloatingActionButton` | Small FAB |
| `CoreFloatingActionButton` | Regular FAB |
| `CoreExtendedFloatingActionButton` | FAB with icon and text |
| `CoreSegmentedButtonGroup` | Segmented button selection |

#### Navigation (`CoreNavBar.kt`)
| Component | Use For |
|-----------|---------|
| `CoreTopAppBar` | Customizable top app bar |
| `CoreBasicAppBar` | Simple app bar with title |
| `CoreBackStackAppBar` | App bar with back button |
| `CoreSearchTopAppBar` | App bar with search action |
| `CoreContentTopAppBar` | App bar with custom content |
| `CoreAppBarAction` | Action icon in app bar |
| `CoreTopBarColor()` | Default app bar colors |

#### Pickers (`CorePickers.kt`)
| Component | Use For |
|-----------|---------|
| `CoreDatePickerInput` | Date selection field |
| `CoreTimePickerInput` | Time selection field |
| `CoreDropdownInput` | Generic dropdown/spinner |
| `CoreSpinnerInput` | String-based dropdown |

#### Selection Controls (`CoreSelectInput.kt`)
| Component | Use For |
|-----------|---------|
| `CoreCheckbox` | Single checkbox with label |
| `CoreCheckboxGroup` | Multiple checkboxes with title |
| `CoreRadioButton` | Single radio button |
| `CoreRadioButtonGroup` | Radio button group |
| `CoreSwitch` | Toggle switch with label |
| `CoreSwitchGroup` | Multiple switches |
| `CoreStateCheckbox` | Tri-state checkbox |

#### Sliders (`CoreSlider.kt`)
| Component | Use For |
|-----------|---------|
| `CoreSlider` | Basic slider |
| `CoreLabelSlider` | Slider with label and value |
| `CorePercentSlider` | 0-100% slider |
| `CoreRangeSlider` | Discrete step slider |
| `CoreSliderMinMax` | Slider with min/max labels |

#### Text Inputs (`CoreTextInput.kt`)
| Component | Use For |
|-----------|---------|
| `CoreTextInput` | Standard text field |
| `CUsernameInput` | Username input |
| `CoreEmailInput` | Email input |
| `CorePasswordInput` | Password with visibility toggle |
| `CorePhoneInput` | Phone number input |
| `CoreSearchInput` | Search field |
| `CoreTextAreaInput` | Multiline text area |

---

## Self-Updating Design System

When you encounter a design requirement that doesn't exist in the current design system, **YOU MUST ASK THE USER FIRST** before proceeding.

### New Color Needed

```
"The design requires a '[color name]' color that doesn't exist in Color.kt.
Would you like me to:
(A) Add a new color to the design system (Light/Dark variants + helper function)?
(B) Use an existing color instead? If so, which one?"
```

**If user says ADD:**
1. Add `Light[Name]` and `Dark[Name]` to `Color.kt`
2. Add to `LightColorScheme` and `DarkColorScheme`
3. Add helper function `get[Name]Color()`
4. Update this SKILL.md reference table

### New Component Needed

```
"The design requires a '[component name]' component that doesn't exist in core/components/.
Would you like me to:
(A) Create a new Core[Name] component in core/components/?
(B) Implement it inline in this feature only?
(C) Use an existing component as alternative?"
```

**If user says CREATE:**
1. Create `Core[Name].kt` in `core/components/`
2. Follow existing component patterns (use theme values)
3. Update this SKILL.md component table

### New Typography Style Needed

```
"The design requires a '[style name]' text style that doesn't exist.
Would you like me to:
(A) Add a new typography style to the design system?
(B) Use existing style with .copy() modifier?
(C) Use an existing style as-is?"
```

### New Spacing Value Needed

```
"The design requires [X]dp spacing not in Dimens.kt.
Would you like me to:
(A) Add a new spacing value?
(B) Use closest existing value? ([closest options])"
```

---

## Quick Reference Tables

### Color Helpers
| Function | Description |
|----------|-------------|
| `getPrimaryColor()` | Primary brand color |
| `getOnPrimaryColor()` | Content on primary |
| `getPrimaryContainerColor()` | Primary container |
| `getOnPrimaryContainerColor()` | Content on primary container |
| `getSecondaryColor()` | Secondary color |
| `getOnSecondaryColor()` | Content on secondary |
| `getSecondaryContainerColor()` | Secondary container |
| `getOnSecondaryContainerColor()` | Content on secondary container |
| `getTertiaryColor()` | Tertiary color |
| `getTertiaryContainerColor()` | Tertiary container |
| `getBackgroundColor()` | Background |
| `getOnBackgroundColor()` | Content on background |
| `getSurfaceColor()` | Surface |
| `getOnSurfaceColor()` | Content on surface |
| `getSurfaceVariantColor()` | Surface variant |
| `getOutlineColor()` | Outline/border |
| `getErrorColor()` | Error state |
| `getOnErrorColor()` | Content on error |
| `getErrorContainerColor()` | Error container |

### Spacing Values
| Object | Value | Size |
|--------|-------|------|
| `Dimens.XXS` | 2dp | Extra extra small |
| `Dimens.XS` | 4dp | Extra small |
| `Dimens.S` | 8dp | Small |
| `Dimens.SM` | 12dp | Small-Medium |
| `Dimens.M` | 16dp | Medium (base) |
| `Dimens.ML` | 20dp | Medium-Large |
| `Dimens.L` | 24dp | Large |
| `Dimens.XL` | 32dp | Extra large |
| `Dimens.XXL` | 40dp | Extra extra large |
| `Dimens.XXXL` | 48dp | Extra extra extra large |
| `Dimens.H` | 56dp | Huge |
| `Dimens.XH` | 64dp | Extra huge |
| `Dimens.XXH` | 72dp | Extra extra huge |
| `Dimens.XXXH` | 80dp | Maximum |

| Semantic | Maps To | Size |
|----------|---------|------|
| `Spacing.Micro` | Dimens.XXS | 2dp |
| `Spacing.Tiny` | Dimens.XS | 4dp |
| `Spacing.ExtraSmall` | Dimens.S | 8dp |
| `Spacing.Small` | Dimens.SM | 12dp |
| `Spacing.Medium` | Dimens.M | 16dp |
| `Spacing.MediumPlus` | Dimens.ML | 20dp |
| `Spacing.Large` | Dimens.L | 24dp |
| `Spacing.ExtraLarge` | Dimens.XL | 32dp |
| `Spacing.Huge` | Dimens.XXL | 40dp |
| `Spacing.Massive` | Dimens.XXXL | 48dp |
| `Spacing.Giant` | Dimens.H | 56dp |
| `Spacing.Enormous` | Dimens.XH | 64dp |
| `Spacing.Colossal` | Dimens.XXH | 72dp |
| `Spacing.Maximum` | Dimens.XXXH | 80dp |

### Component Dimensions
| Value | Size | Use For |
|-------|------|---------|
| `ComponentDimens.ButtonHeightSmall` | 32dp | Small buttons |
| `ComponentDimens.ButtonHeightMedium` | 40dp | Default buttons |
| `ComponentDimens.ButtonHeightLarge` | 48dp | Large buttons |
| `ComponentDimens.IconSizeSmall` | 16dp | Small icons |
| `ComponentDimens.IconSizeMedium` | 24dp | Default icons |
| `ComponentDimens.IconSizeLarge` | 32dp | Large icons |
| `ComponentDimens.CardCornerRadius` | 12dp | Card corners |
| `ComponentDimens.TextFieldHeight` | 56dp | Input height |
| `ComponentDimens.TopAppBarHeight` | 64dp | Top bar height |

### Typography Helpers
| Function | Size | Weight |
|----------|------|--------|
| `getTextDisplayLarge()` | 57sp | Normal |
| `getTextDisplayMedium()` | 45sp | Normal |
| `getTextDisplaySmall()` | 36sp | Normal |
| `getTextHeadlineLarge()` | 32sp | Normal |
| `getTextHeadlineMedium()` | 28sp | Normal |
| `getTextHeadlineSmall()` | 24sp | Normal |
| `getTextTitleLarge()` | 22sp | Normal |
| `getTextTitleMedium()` | 16sp | Medium |
| `getTextTitleSmall()` | 14sp | Medium |
| `getTextBodyLarge()` | 16sp | Normal |
| `getTextBodyMedium()` | 14sp | Normal |
| `getTextBodySmall()` | 12sp | Normal |
| `getTextLabelLarge()` | 14sp | Medium |
| `getTextLabelMedium()` | 12sp | Medium |
| `getTextLabelSmall()` | 11sp | Medium |

---

## Verification Checklist

Before completing any UI implementation, verify:

- [ ] All colors use theme helper functions (`get*Color()`)
- [ ] All spacing uses `Spacing.*` or `Dimens.*` objects
- [ ] All typography uses theme helpers (`getText*()`)
- [ ] All buttons use `Core*Button` variants
- [ ] All text inputs use `Core*Input` variants
- [ ] All app bars use `Core*AppBar` variants
- [ ] All selection controls use `Core*` variants
- [ ] No hardcoded dp, sp, or Color values
- [ ] Proper imports from `core.theme.*` and `core.components.*`
- [ ] Asked user before adding any new design system elements
- [ ] Updated this SKILL.md if new elements were added

---

<!-- LAST_UPDATED: 2026-02-05 -->
