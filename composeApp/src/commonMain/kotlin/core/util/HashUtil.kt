package core.util

import kotlin.experimental.and

/**
 * Utility object for cryptographic hashing operations.
 */
object HashUtil {
    /**
     * Generates MD5 hash of the input string.
     * 
     * @param input String to hash
     * @return MD5 hash as hexadecimal string
     */
    fun md5(input: String): String {
        val bytes = input.encodeToByteArray()
        val digest = md5Digest(bytes)
        return digest.joinToString("") { byte ->
            (byte.toInt() and 0xFF).toString(16).padStart(2, '0')
        }
    }
    
    private fun md5Digest(input: ByteArray): ByteArray {
        // MD5 implementation using standard algorithm
        val md5 = MD5()
        return md5.digest(input)
    }
    
    private class MD5 {
        private val buffer = ByteArray(64)
        private var bufferLength = 0
        private var messageLength = 0L
        
        private var a = 0x67452301
        private var b = 0xEFCDAB89.toInt()
        private var c = 0x98BADCFE.toInt()
        private var d = 0x10325476
        
        fun digest(input: ByteArray): ByteArray {
            for (byte in input) {
                update(byte)
            }
            finish()
            
            return byteArrayOf(
                (a and 0xFF).toByte(),
                ((a shr 8) and 0xFF).toByte(),
                ((a shr 16) and 0xFF).toByte(),
                ((a shr 24) and 0xFF).toByte(),
                (b and 0xFF).toByte(),
                ((b shr 8) and 0xFF).toByte(),
                ((b shr 16) and 0xFF).toByte(),
                ((b shr 24) and 0xFF).toByte(),
                (c and 0xFF).toByte(),
                ((c shr 8) and 0xFF).toByte(),
                ((c shr 16) and 0xFF).toByte(),
                ((c shr 24) and 0xFF).toByte(),
                (d and 0xFF).toByte(),
                ((d shr 8) and 0xFF).toByte(),
                ((d shr 16) and 0xFF).toByte(),
                ((d shr 24) and 0xFF).toByte()
            )
        }
        
        private fun update(byte: Byte) {
            buffer[bufferLength++] = byte
            messageLength++
            
            if (bufferLength == 64) {
                processBlock()
                bufferLength = 0
            }
        }
        
        private fun finish() {
            val messageLengthInBits = messageLength * 8
            
            update(0x80.toByte())
            while (bufferLength != 56) {
                update(0)
            }
            
            for (i in 0..7) {
                update(((messageLengthInBits shr (i * 8)) and 0xFF).toByte())
            }
        }
        
        private fun processBlock() {
            val x = IntArray(16)
            for (i in 0..15) {
                x[i] = (buffer[i * 4].toInt() and 0xFF) or
                       ((buffer[i * 4 + 1].toInt() and 0xFF) shl 8) or
                       ((buffer[i * 4 + 2].toInt() and 0xFF) shl 16) or
                       ((buffer[i * 4 + 3].toInt() and 0xFF) shl 24)
            }
            
            var aa = a
            var bb = b
            var cc = c
            var dd = d
            
            // Round 1
            aa = ff(aa, bb, cc, dd, x[0], 7, 0xD76AA478.toInt())
            dd = ff(dd, aa, bb, cc, x[1], 12, 0xE8C7B756.toInt())
            cc = ff(cc, dd, aa, bb, x[2], 17, 0x242070DB)
            bb = ff(bb, cc, dd, aa, x[3], 22, 0xC1BDCEEE.toInt())
            aa = ff(aa, bb, cc, dd, x[4], 7, 0xF57C0FAF.toInt())
            dd = ff(dd, aa, bb, cc, x[5], 12, 0x4787C62A)
            cc = ff(cc, dd, aa, bb, x[6], 17, 0xA8304613.toInt())
            bb = ff(bb, cc, dd, aa, x[7], 22, 0xFD469501.toInt())
            aa = ff(aa, bb, cc, dd, x[8], 7, 0x698098D8)
            dd = ff(dd, aa, bb, cc, x[9], 12, 0x8B44F7AF.toInt())
            cc = ff(cc, dd, aa, bb, x[10], 17, 0xFFFF5BB1.toInt())
            bb = ff(bb, cc, dd, aa, x[11], 22, 0x895CD7BE.toInt())
            aa = ff(aa, bb, cc, dd, x[12], 7, 0x6B901122)
            dd = ff(dd, aa, bb, cc, x[13], 12, 0xFD987193.toInt())
            cc = ff(cc, dd, aa, bb, x[14], 17, 0xA679438E.toInt())
            bb = ff(bb, cc, dd, aa, x[15], 22, 0x49B40821)
            
            // Round 2
            aa = gg(aa, bb, cc, dd, x[1], 5, 0xF61E2562.toInt())
            dd = gg(dd, aa, bb, cc, x[6], 9, 0xC040B340.toInt())
            cc = gg(cc, dd, aa, bb, x[11], 14, 0x265E5A51)
            bb = gg(bb, cc, dd, aa, x[0], 20, 0xE9B6C7AA.toInt())
            aa = gg(aa, bb, cc, dd, x[5], 5, 0xD62F105D.toInt())
            dd = gg(dd, aa, bb, cc, x[10], 9, 0x02441453)
            cc = gg(cc, dd, aa, bb, x[15], 14, 0xD8A1E681.toInt())
            bb = gg(bb, cc, dd, aa, x[4], 20, 0xE7D3FBC8.toInt())
            aa = gg(aa, bb, cc, dd, x[9], 5, 0x21E1CDE6)
            dd = gg(dd, aa, bb, cc, x[14], 9, 0xC33707D6.toInt())
            cc = gg(cc, dd, aa, bb, x[3], 14, 0xF4D50D87.toInt())
            bb = gg(bb, cc, dd, aa, x[8], 20, 0x455A14ED)
            aa = gg(aa, bb, cc, dd, x[13], 5, 0xA9E3E905.toInt())
            dd = gg(dd, aa, bb, cc, x[2], 9, 0xFCEFA3F8.toInt())
            cc = gg(cc, dd, aa, bb, x[7], 14, 0x676F02D9)
            bb = gg(bb, cc, dd, aa, x[12], 20, 0x8D2A4C8A.toInt())
            
            // Round 3
            aa = hh(aa, bb, cc, dd, x[5], 4, 0xFFFA3942.toInt())
            dd = hh(dd, aa, bb, cc, x[8], 11, 0x8771F681.toInt())
            cc = hh(cc, dd, aa, bb, x[11], 16, 0x6D9D6122)
            bb = hh(bb, cc, dd, aa, x[14], 23, 0xFDE5380C.toInt())
            aa = hh(aa, bb, cc, dd, x[1], 4, 0xA4BEEA44.toInt())
            dd = hh(dd, aa, bb, cc, x[4], 11, 0x4BDECFA9)
            cc = hh(cc, dd, aa, bb, x[7], 16, 0xF6BB4B60.toInt())
            bb = hh(bb, cc, dd, aa, x[10], 23, 0xBEBFBC70.toInt())
            aa = hh(aa, bb, cc, dd, x[13], 4, 0x289B7EC6)
            dd = hh(dd, aa, bb, cc, x[0], 11, 0xEAA127FA.toInt())
            cc = hh(cc, dd, aa, bb, x[3], 16, 0xD4EF3085.toInt())
            bb = hh(bb, cc, dd, aa, x[6], 23, 0x04881D05)
            aa = hh(aa, bb, cc, dd, x[9], 4, 0xD9D4D039.toInt())
            dd = hh(dd, aa, bb, cc, x[12], 11, 0xE6DB99E5.toInt())
            cc = hh(cc, dd, aa, bb, x[15], 16, 0x1FA27CF8)
            bb = hh(bb, cc, dd, aa, x[2], 23, 0xC4AC5665.toInt())
            
            // Round 4
            aa = ii(aa, bb, cc, dd, x[0], 6, 0xF4292244.toInt())
            dd = ii(dd, aa, bb, cc, x[7], 10, 0x432AFF97)
            cc = ii(cc, dd, aa, bb, x[14], 15, 0xAB9423A7.toInt())
            bb = ii(bb, cc, dd, aa, x[5], 21, 0xFC93A039.toInt())
            aa = ii(aa, bb, cc, dd, x[12], 6, 0x655B59C3)
            dd = ii(dd, aa, bb, cc, x[3], 10, 0x8F0CCC92.toInt())
            cc = ii(cc, dd, aa, bb, x[10], 15, 0xFFEFF47D.toInt())
            bb = ii(bb, cc, dd, aa, x[1], 21, 0x85845DD1.toInt())
            aa = ii(aa, bb, cc, dd, x[8], 6, 0x6FA87E4F)
            dd = ii(dd, aa, bb, cc, x[15], 10, 0xFE2CE6E0.toInt())
            cc = ii(cc, dd, aa, bb, x[6], 15, 0xA3014314.toInt())
            bb = ii(bb, cc, dd, aa, x[13], 21, 0x4E0811A1)
            aa = ii(aa, bb, cc, dd, x[4], 6, 0xF7537E82.toInt())
            dd = ii(dd, aa, bb, cc, x[11], 10, 0xBD3AF235.toInt())
            cc = ii(cc, dd, aa, bb, x[2], 15, 0x2AD7D2BB)
            bb = ii(bb, cc, dd, aa, x[9], 21, 0xEB86D391.toInt())
            
            a += aa
            b += bb
            c += cc
            d += dd
        }
        
        private fun ff(a: Int, b: Int, c: Int, d: Int, x: Int, s: Int, ac: Int): Int {
            var temp = a + ((b and c) or (b.inv() and d)) + x + ac
            temp = (temp shl s) or (temp ushr (32 - s))
            return temp + b
        }
        
        private fun gg(a: Int, b: Int, c: Int, d: Int, x: Int, s: Int, ac: Int): Int {
            var temp = a + ((b and d) or (c and d.inv())) + x + ac
            temp = (temp shl s) or (temp ushr (32 - s))
            return temp + b
        }
        
        private fun hh(a: Int, b: Int, c: Int, d: Int, x: Int, s: Int, ac: Int): Int {
            var temp = a + (b xor c xor d) + x + ac
            temp = (temp shl s) or (temp ushr (32 - s))
            return temp + b
        }
        
        private fun ii(a: Int, b: Int, c: Int, d: Int, x: Int, s: Int, ac: Int): Int {
            var temp = a + (c xor (b or d.inv())) + x + ac
            temp = (temp shl s) or (temp ushr (32 - s))
            return temp + b
        }
    }
}
