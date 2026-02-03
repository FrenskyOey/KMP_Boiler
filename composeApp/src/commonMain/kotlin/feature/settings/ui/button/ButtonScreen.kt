package feature.settings.ui.button
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import core.components.CButton
import core.components.CExtendedFloatingActionButton
import core.components.CFloatingActionButton
import core.components.COutlinedButton
import core.components.CSegmentedButtonGroup
import core.components.CSmallFloatingActionButton
import core.components.CTextButton
import core.components.CTonalButton
import core.theme.Spacing
import core.theme.getOnBackgroundColor
import core.theme.getSurfaceColor
import core.theme.getTextHeadlineSmall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Button Style",
                        style = getTextHeadlineSmall().copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getSurfaceColor(),
                    titleContentColor = getOnBackgroundColor(),
                    navigationIconContentColor = getOnBackgroundColor()
                )
            )
        },
        containerColor = getSurfaceColor()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(Spacing.Medium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Spacing.Large)
        ) {

            // Segmented Buttons Section
            SegmentedButtonsSection()
            Spacer(modifier = Modifier.height(Spacing.Tiny))
            // Filled Buttons Section
            FilledButtonsSection()
            Spacer(modifier = Modifier.height(Spacing.Tiny))
            // Tonal Buttons Section
            TonalButtonsSection()
            Spacer(modifier = Modifier.height(Spacing.Tiny))
            // Outlined Buttons Section
            OutlinedButtonsSection()
            Spacer(modifier = Modifier.height(Spacing.Tiny))
            // Text Buttons Section
            TextButtonsSection()
            Spacer(modifier = Modifier.height(Spacing.Tiny))
            // FAB Variants Section
            FABVariantsSection()
        }
    }
}


@Composable
private fun SegmentedButtonsSection() {
    var selectedPeriod by remember { mutableStateOf("Day") }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "Segmented Buttons",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        CSegmentedButtonGroup(
            options = listOf("Day", "Week", "Month"),
            selectedOption = selectedPeriod,
            onOptionSelected = { selectedPeriod = it },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun FilledButtonsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "Filled Buttons",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            modifier = Modifier.fillMaxWidth()
        ) {
            CButton(
                text = "Enabled",
                onClick = { /* Handle click */ }
            )

            CButton(
                text = "With Icon",
                onClick = { /* Handle click */ },
                icon = Icons.Default.Add
            )
        }

        CButton(
            text = "Disabled",
            onClick = { /* Handle click */ },
            enabled = false
        )
    }
}

@Composable
private fun TonalButtonsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "Tonal Buttons",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            modifier = Modifier.fillMaxWidth()
        ) {
            CTonalButton(
                text = "Enabled",
                onClick = { /* Handle click */ }
            )

            CTonalButton(
                text = "Icon",
                onClick = { /* Handle click */ },
                icon = Icons.Default.Star
            )
        }
    }
}

@Composable
private fun OutlinedButtonsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "Outlined Buttons",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            modifier = Modifier.fillMaxWidth()
        ) {
            COutlinedButton(
                text = "Outline",
                onClick = { /* Handle click */ }
            )

            COutlinedButton(
                text = "Edit",
                onClick = { /* Handle click */ },
                icon = Icons.Default.Edit
            )
        }
    }
}

@Composable
private fun TextButtonsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "Text Buttons",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            modifier = Modifier.fillMaxWidth()
        ) {
            CTextButton(
                text = "Cancel",
                onClick = { /* Handle click */ }
            )

            CTextButton(
                text = "Settings",
                onClick = { /* Handle click */ },
                icon = Icons.Default.Settings
            )
        }
    }
}

@Composable
private fun FABVariantsSection() {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.Small)) {
        Text(
            text = "FAB Variants",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Small),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
            ) {
                CSmallFloatingActionButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Add",
                    onClick = { /* Handle click */ }
                )
                Text(
                    text = "Small",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
            ) {
                CFloatingActionButton(
                    icon = Icons.Default.Edit,
                    contentDescription = "Edit",
                    onClick = { /* Handle click */ }
                )
                Text(
                    text = "Regular",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Tiny)
            ) {
                CExtendedFloatingActionButton(
                    text = "Compose",
                    icon = Icons.Default.Email,
                    onClick = { /* Handle click */ }
                )
                Text(
                    text = "Extended",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
