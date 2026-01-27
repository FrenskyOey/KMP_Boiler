package core.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import core.theme.getOnSurfaceColor
import core.theme.getPrimaryColor
import core.theme.getSecondaryColor
import core.theme.getSurfaceVariantColor
import core.theme.getTextBodyMedium
import core.theme.getTextBodySmall
import core.theme.getTextHeadlineLarge
import core.theme.getTextLabelSmall
import core.theme.getTextTitleMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    flavorName: String,
    apiUrl: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KMP Learn") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = getPrimaryColor()
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Fixed: use verticalArrangement instead of verticalAlignment
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Text(
                text = "KMP LEARN PROJECT",
                style = getTextHeadlineLarge(),
                color = getPrimaryColor()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Display current flavor
            Text(
                text = "Flavor: $flavorName",
                style = getTextTitleMedium(),
                color = getSecondaryColor()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display API URL (for verification)
            Text(
                text = "API: $apiUrl",
                style = getTextBodySmall(),
                color = getOnSurfaceColor().copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Status indicator
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getSurfaceVariantColor()
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color.Green,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "✅ Phase 1 Boilerplate Complete",
                        style = getTextBodyMedium(),
                        color = getOnSurfaceColor()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Architecture: Feature-First",
                        style = getTextLabelSmall(),
                        color = getOnSurfaceColor().copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
