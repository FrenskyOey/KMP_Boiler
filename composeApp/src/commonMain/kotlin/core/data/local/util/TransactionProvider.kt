package core.data.local.util

interface TransactionProvider {
    suspend fun <R> runAsTransaction(block: suspend () -> R): R
}
