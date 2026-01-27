package core.di

import org.koin.dsl.module

// We need a way to create the database. 
// Since this is commonMain, we can't instantiate the platform specific builder directly
// unless we use an expect function or pass a factory.
// The prompt had `single { createDatabase(get()) }`.
// I will just create a placeholder function for now or comment it out for Phase 1 to compile.
// Or better, define the expect function.

val coreDatabaseModule = module {
    // single { createDatabase(get()) } 
    // Commented out until we have platform specific implementations for createDatabase
}
