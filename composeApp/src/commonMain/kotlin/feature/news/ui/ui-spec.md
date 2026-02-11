# UI Specification - News Detail

## Design Requirements
*To be populated iteratively based on user input.*

### 1. Top Bar
- **Component**: `CoreTopAppBar` (from `core.components`) -> *`CoreBackStackAppBar` does not support actions*.
- **Title**: `articleTitle` (passed via navigation argument).
- **Navigation Action**:
    - Icon: `Icons.AutoMirrored.Filled.ArrowBack`
    - Action: Pop back stack (`onBackClick`).
- **Menu Actions**:
    - **Share**:
        - Icon: `Icons.Default.Share`
        - Action: Copy content URL to Clipboard.
- **Behavior**: Standard pinned top bar.

### 2. Header & Title (Hero Section)
- **Container**: `Column` (Scrollable).
- **Header Image**:
    - **Component**: `AsyncImage` (Coil 3).
    - **Size**: Fill Width, Aspect Ratio 16:9.
    - **Styling**: Rounded Corners (e.g., `12.dp` or matching theme), Crop Center.
    - **Placeholder/Error**: Gray background or error icon.
- **Topic/Category**:
    - **Placement**: Below Image, before Title.
    - **Style**: Small Capsule/Chip or Text with distinct color (e.g., Primary/Secondary).
- **Title**:
    - **Placement**: Below Topic.
    - **Content**: Full article title.
    - **Style**: `HeadlineMedium` or `HeadlineSmall`, Bold.
    - **Layout**: Wrap content (multiline).
- **Author Section**:
    - **Placement**: Below Title.
    - **Layout**: `Row` (Vertical Center).
    - **Components**:
        - **Avatar**: New `CoreAvatar` component.
            - Circle Shape.
            - `AsyncImage` for URL.
            - Fallback: Colored background with Author Name Initials.
        - **Info Column**:
            - Author Name (`TitleMedium`, Bold).
            - Metadata Row: "Publication • Time • ReadTime" (`LabelMedium`, Gray).

### 4. Content Body
- **Container**: `Column` (Padding Horizontal).
- **Paragraph**:
    - Style: `BodyLarge`.
    - Spacing: Standard paragraph spacing (e.g., `16.dp`).
- **Quote**:
    - Style: `BodyLarge` + Italic.
    - Decoration: Vertical Bar on Start (Primary Color, `4.dp` width).
    - Padding: Start padding to separate text from bar.

### 5. Interaction & Footer
- **Tags**:
    - **Placement**: Bottom of content.
    - **Container**: `FlowRow` or `LazyRow`? -> *FlowRow preferred for tags*.
    - **Style**: Rounded Chips (Surface Variant background, OnSurfaceVariant text).
    - **Interaction**: None (No linking yet).
- **Footer**: Spacer to ensure content isn't hidden behind navigation bars if any.

### 6. Loading & Error States
- **Loading**:
    - **Container**: `Column` (Center Aligned, Fill Max Size).
    - **Visual**: `CircularProgressIndicator` (Primary Color).
    - **Text**:
        - Title: "Loading article..." (`TitleMedium`, Bold).
        - Subtitle: "We are fetching the latest updates for you.\nThis will only take a moment." (`BodyMedium`, Gray, Center Align).
    - **Spacing**: 8.dp between elements.

- **Error**:
    - **Container**: `Column` (Center Aligned, Fill Max Size, Padding 16.dp).
    - **Visual**: Error Icon (e.g., `Icons.Default.CloudOff`) in a Light/SurfaceVariant Circle container.
    - **Text**:
        - Title: "Failed to load article" (`TitleMedium`, Bold).
        - Subtitle: "We had trouble fetching the news story.\nPlease check your connection and try again." (`BodyMedium`, Gray, Center Align).
    - **Action**: "Retry" Button (`Button`, Full Width or wide).
        - Text: "Retry"
        - Action: Triggers `OnRetry` event.
