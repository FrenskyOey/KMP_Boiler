package feature.news.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmp_learn.composeapp.generated.resources.Res
import kmp_learn.composeapp.generated.resources.ic_home
import kmp_learn.composeapp.generated.resources.ic_newspaper
import org.jetbrains.compose.resources.painterResource

@Composable
fun NewsEmptyWidget(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Icon container with concentric circles
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(280.dp)
            ) {
                // Outer circle (light gray)
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EAF0))
                )
                
                // Inner circle (light blue)
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD4E7F7))
                )
                
                // Newspaper icon
                Icon(
                    painter = painterResource(Res.drawable.ic_newspaper),
                    contentDescription = "No news",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(80.dp)
                )
                
                // Small sparkle icon in bottom right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-20).dp, y = (-20).dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_home),
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Title text
            Text(
                text = "No news for today",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Description text
            Text(
                text = "We'll let you know when something new arrives. Check back later for updates.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B7280),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}
