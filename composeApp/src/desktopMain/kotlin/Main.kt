import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.interview.prep.kmp_learn.App
import core.di.coreDatabaseModule
import core.di.coreNetworkModule
import core.di.corePreferencesModule
import core.di.coreConfigModule
import core.di.secureStorageModule
import di.appModule
import feature.news.di.newsModule
import feature.onboarding.di.onboardingModule
import feature.settings.di.settingsModule
import org.koin.core.context.startKoin

fun main() = application {
    // Initialize Koin for desktop
    startKoin {
        modules(
            // Core modules
            coreNetworkModule,
            coreDatabaseModule,
            corePreferencesModule,
            corePreferencesModule,
            coreConfigModule,
            secureStorageModule,
            
            // Feature modules
            newsModule,
            settingsModule,
            onboardingModule,
            
            // App module
            appModule
        )
    }
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "KMP Learn - Hot Reload Dashboard",
        state = rememberWindowState(width = 400.dp, height = 800.dp),
        alwaysOnTop = true,
    ) {
        App()
    }
}
