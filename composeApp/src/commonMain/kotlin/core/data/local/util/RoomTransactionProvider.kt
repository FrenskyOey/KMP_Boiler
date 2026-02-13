package core.data.local.util

import androidx.room.withTransaction
import core.data.local.database.AppDatabase

class RoomTransactionProvider(
    private val database: AppDatabase
) : TransactionProvider {
    override suspend fun <R> runAsTransaction(block: suspend () -> R): R {
        return database.withTransaction(block)
    }
}
