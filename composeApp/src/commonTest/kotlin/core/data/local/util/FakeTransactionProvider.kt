package core.data.local.util

class FakeTransactionProvider : TransactionProvider {
    
    var transactionCalled = false

    override suspend fun <R> runAsTransaction(block: suspend () -> R): R {
        transactionCalled = true
        return block() // Just execute immediately without wrapping
    }
}
