package com.trustwallet.core.app.blockchains.terra

import com.google.protobuf.ByteString
import com.trustwallet.core.app.utils.toHexByteArray
import org.junit.Assert.assertEquals
import org.junit.Test
import wallet.core.jni.*
import wallet.core.jni.CoinType.TERRAV2
import wallet.core.jni.proto.Cosmos
import wallet.core.jni.proto.Cosmos.SigningOutput
import wallet.core.jni.proto.Cosmos.SigningMode
import wallet.core.java.AnySigner

class TestTerraTransactions {

    init {
        System.loadLibrary("TrustWalletCore")
    }

    @Test
    fun testSigningTransaction() {
        val key =
            PrivateKey("80e81ea269e66a0a05b11236df7919fb7fbeedba87452d667489d7403a02f005".toHexByteArray())
        val publicKey = key.getPublicKeySecp256k1(true)
        val from = AnyAddress(publicKey, TERRAV2).description()

        val txAmount = Cosmos.Amount.newBuilder().apply {
            amount = "1000000"
            denom = "uluna"
        }.build()

        val sendCoinsMsg = Cosmos.Message.Send.newBuilder().apply {
            fromAddress = from
            toAddress = "terra1jlgaqy9nvn2hf5t2sra9ycz8s77wnf9l0kmgcp"
            addAllAmounts(listOf(txAmount))
        }.build()

        val message = Cosmos.Message.newBuilder().apply {
            sendCoinsMessage = sendCoinsMsg
        }.build()

        val feeAmount = Cosmos.Amount.newBuilder().apply {
            amount = "30000"
            denom = "uluna"
        }.build()

        val cosmosFee = Cosmos.Fee.newBuilder().apply {
            gas = 200000
            addAllAmounts(listOf(feeAmount))
        }.build()

        val signingInput = Cosmos.SigningInput.newBuilder().apply {
            signingMode = SigningMode.Protobuf
            accountNumber = 1037
            chainId = "phoenix-1"
            memo = ""
            sequence = 1
            fee = cosmosFee
            privateKey = ByteString.copyFrom(key.data())
            addAllMessages(listOf(message))
        }.build()

        val output = AnySigner.sign(signingInput, TERRAV2, SigningOutput.parser())
        val jsonPayload = output.json

        assertEquals(output.serialized, "{\"mode\":\"BROADCAST_MODE_BLOCK\",\"tx_bytes\":\"CpEBCo4BChwvY29zbW9zLmJhbmsudjFiZXRhMS5Nc2dTZW5kEm4KLHRlcnJhMWhzazZqcnl5cWpmaHA1ZGhjNTV0YzlqdGNreWd4MGVwMzdoZGQyEix0ZXJyYTFqbGdhcXk5bnZuMmhmNXQyc3JhOXljejhzNzd3bmY5bDBrbWdjcBoQCgV1bHVuYRIHMTAwMDAwMBJoClAKRgofL2Nvc21vcy5jcnlwdG8uc2VjcDI1NmsxLlB1YktleRIjCiECVyhuw/N9M1V7u6oACyd0SskCOqmWfK51oYHR/5H6ncUSBAoCCAEYARIUCg4KBXVsdW5hEgUzMDAwMBDAmgwaQPh0C3rjzdixIUiyPx3FlWAxzbKILNAcSRVeQnaTl1vsI5DEfYa2oYlUBLqyilcMCcU/iaJLhex30No2ak0Zn1Q=\"}")
        assertEquals(output.error, "")
    }

    @Test
    fun testSigningWasmTerraTransferTx() {
        val key =
            PrivateKey("cf08ee8493e6f6a53f9721b9045576e80f371c0e36d08fdaf78b27a7afd8e616".toHexByteArray())
        val publicKey = key.getPublicKeySecp256k1(true)
        val from = AnyAddress(publicKey, TERRAV2).description()

        val wasmTransferMessage = Cosmos.Message.WasmTerraExecuteContractTransfer.newBuilder().apply {
            senderAddress = from
            contractAddress = "terra14z56l0fp2lsf86zy3hty2z47ezkhnthtr9yq76"
            amount = ByteString.copyFrom("0x3D090".toHexByteArray()) // 250000
            recipientAddress = "terra1jlgaqy9nvn2hf5t2sra9ycz8s77wnf9l0kmgcp"
        }.build()

        val message = Cosmos.Message.newBuilder().apply {
            wasmTerraExecuteContractTransferMessage = wasmTransferMessage
        }.build()

        val feeAmount = Cosmos.Amount.newBuilder().apply {
            amount = "3000"
            denom = "uluna"
        }.build()

        val cosmosFee = Cosmos.Fee.newBuilder().apply {
            gas = 200000
            addAllAmounts(listOf(feeAmount))
        }.build()

        val signingInput = Cosmos.SigningInput.newBuilder().apply {
            signingMode = SigningMode.Protobuf
            accountNumber = 3407705
            chainId = "phoenix-1"
            memo = ""
            sequence = 3
            fee = cosmosFee
            privateKey = ByteString.copyFrom(key.data())
            addAllMessages(listOf(message))
        }.build()

        val output = AnySigner.sign(signingInput, TERRAV2, SigningOutput.parser())

        assertEquals(output.serialized, "{\"mode\":\"BROADCAST_MODE_BLOCK\",\"tx_bytes\":\"CuUBCuIBCiQvY29zbXdhc20ud2FzbS52MS5Nc2dFeGVjdXRlQ29udHJhY3QSuQEKLHRlcnJhMTh3dWtwODRkcTIyN3d1NG1naDBqbTZuOW5sbmo2cnM4MnBwOXdmEix0ZXJyYTE0ejU2bDBmcDJsc2Y4Nnp5M2h0eTJ6NDdlemtobnRodHI5eXE3NhpbeyJ0cmFuc2ZlciI6eyJhbW91bnQiOiIyNTAwMDAiLCJyZWNpcGllbnQiOiJ0ZXJyYTFqbGdhcXk5bnZuMmhmNXQyc3JhOXljejhzNzd3bmY5bDBrbWdjcCJ9fRJnClAKRgofL2Nvc21vcy5jcnlwdG8uc2VjcDI1NmsxLlB1YktleRIjCiEDcGY6x7D5iSlv61zln7pKUNfpThziVt/yJRRZyizZrAASBAoCCAEYAxITCg0KBXVsdW5hEgQzMDAwEMCaDBpAiBGbQaj+jsXE6/FssD3fC77QOxpli9GqsPea+KoNyMIEgVj89Hii+oU1bAEQS4qV0SaE2V6RNy24uCcFTIRbcQ==\"}")
        assertEquals(output.error, "")
    }

    @Test
    fun testSigningWasmTerraGeneric() {
        val key =
            PrivateKey("cf08ee8493e6f6a53f9721b9045576e80f371c0e36d08fdaf78b27a7afd8e616".toHexByteArray())
        val publicKey = key.getPublicKeySecp256k1(true)
        val from = AnyAddress(publicKey, TERRAV2).description()

        val wasmGenericMessage = Cosmos.Message.WasmTerraExecuteContractGeneric.newBuilder().apply {
            senderAddress = from
            contractAddress = "terra14z56l0fp2lsf86zy3hty2z47ezkhnthtr9yq76"
            executeMsg = """{"transfer": { "amount": "250000", "recipient": "terra1d7048csap4wzcv5zm7z6tdqem2agyp9647vdyj" } }"""
        }.build()

        val message = Cosmos.Message.newBuilder().apply {
            wasmTerraExecuteContractGeneric = wasmGenericMessage
        }.build()

        val feeAmount = Cosmos.Amount.newBuilder().apply {
            amount = "3000"
            denom = "uluna"
        }.build()

        val cosmosFee = Cosmos.Fee.newBuilder().apply {
            gas = 200000
            addAllAmounts(listOf(feeAmount))
        }.build()

        val signingInput = Cosmos.SigningInput.newBuilder().apply {
            signingMode = SigningMode.Protobuf
            accountNumber = 3407705
            chainId = "phoenix-1"
            memo = ""
            sequence = 7
            fee = cosmosFee
            privateKey = ByteString.copyFrom(key.data())
            addAllMessages(listOf(message))
        }.build()

        val output = AnySigner.sign(signingInput, TERRAV2, SigningOutput.parser())

        assertEquals(output.serialized, "{\"mode\":\"BROADCAST_MODE_BLOCK\",\"tx_bytes\":\"CuwBCukBCiQvY29zbXdhc20ud2FzbS52MS5Nc2dFeGVjdXRlQ29udHJhY3QSwAEKLHRlcnJhMTh3dWtwODRkcTIyN3d1NG1naDBqbTZuOW5sbmo2cnM4MnBwOXdmEix0ZXJyYTE0ejU2bDBmcDJsc2Y4Nnp5M2h0eTJ6NDdlemtobnRodHI5eXE3NhpieyJ0cmFuc2ZlciI6IHsgImFtb3VudCI6ICIyNTAwMDAiLCAicmVjaXBpZW50IjogInRlcnJhMWQ3MDQ4Y3NhcDR3emN2NXptN3o2dGRxZW0yYWd5cDk2NDd2ZHlqIiB9IH0SZwpQCkYKHy9jb3Ntb3MuY3J5cHRvLnNlY3AyNTZrMS5QdWJLZXkSIwohA3BmOsew+Ykpb+tc5Z+6SlDX6U4c4lbf8iUUWcos2awAEgQKAggBGAcSEwoNCgV1bHVuYRIEMzAwMBDAmgwaQGlYzOoAu/PfyCTSTisGJVW9KWwifxMbCmzy2xwqNg+ZHQkDjVRyUBl7gmbXXLzdOMqtwF1CMauJhlGwmEdzhK4=\"}")
        assertEquals(output.error, "")
    }

    @Test
    fun testSigningWasmTerraGenericWithCoins() {
        val key =
            PrivateKey("cf08ee8493e6f6a53f9721b9045576e80f371c0e36d08fdaf78b27a7afd8e616".toHexByteArray())
        val publicKey = key.getPublicKeySecp256k1(true)
        val from = AnyAddress(publicKey, TERRAV2).description()

        val coins = Cosmos.Amount.newBuilder().apply {
            amount = "1000"
            denom = "uusd"
        }.build()

        val wasmGenericMessage = Cosmos.Message.WasmTerraExecuteContractGeneric.newBuilder().apply {
            senderAddress = from
            contractAddress = "terra1sepfj7s0aeg5967uxnfk4thzlerrsktkpelm5s"
            executeMsg = """{ "deposit_stable": {} }"""
            addCoins(coins)
        }.build()

        val message = Cosmos.Message.newBuilder().apply {
            wasmTerraExecuteContractGeneric = wasmGenericMessage
        }.build()

        val feeAmount = Cosmos.Amount.newBuilder().apply {
            amount = "7000"
            denom = "uluna"
        }.build()

        val cosmosFee = Cosmos.Fee.newBuilder().apply {
            gas = 600000
            addAllAmounts(listOf(feeAmount))
        }.build()

        val signingInput = Cosmos.SigningInput.newBuilder().apply {
            signingMode = SigningMode.Protobuf
            accountNumber = 3407705
            chainId = "phoenix-1"
            memo = ""
            sequence = 9
            fee = cosmosFee
            privateKey = ByteString.copyFrom(key.data())
            addAllMessages(listOf(message))
        }.build()

        val output = AnySigner.sign(signingInput, TERRAV2, SigningOutput.parser())

        assertEquals(output.serialized, "{\"mode\":\"BROADCAST_MODE_BLOCK\",\"tx_bytes\":\"CrABCq0BCiQvY29zbXdhc20ud2FzbS52MS5Nc2dFeGVjdXRlQ29udHJhY3QShAEKLHRlcnJhMTh3dWtwODRkcTIyN3d1NG1naDBqbTZuOW5sbmo2cnM4MnBwOXdmEix0ZXJyYTFzZXBmajdzMGFlZzU5Njd1eG5mazR0aHpsZXJyc2t0a3BlbG01cxoYeyAiZGVwb3NpdF9zdGFibGUiOiB7fSB9KgwKBHV1c2QSBDEwMDASZwpQCkYKHy9jb3Ntb3MuY3J5cHRvLnNlY3AyNTZrMS5QdWJLZXkSIwohA3BmOsew+Ykpb+tc5Z+6SlDX6U4c4lbf8iUUWcos2awAEgQKAggBGAkSEwoNCgV1bHVuYRIENzAwMBDAzyQaQEDA2foXegF+rslj6o8bX2HPJfn+q/6Ezbq2iAd0SFOTQqS8aAyywQkdZJRToXcaby1HOYL1WvmsMPgrFzChiY4=\"}")
        assertEquals(output.error, "")
    }
}
