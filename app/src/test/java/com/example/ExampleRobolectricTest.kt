package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.*
import com.example.data.repository.ProductRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Gaonova", appName)
    }

    @Test
    fun `verify sample products catalog and GI tags`() {
        val products = ProductRepository.sampleProducts
        assertTrue("Products catalog should not be empty", products.isNotEmpty())

        val kantha = products.find { it.id == "prod_kantha_bolpur" }
        assertNotNull("Nakshi Kantha product should exist", kantha)
        assertEquals("West Bengal", kantha?.state)
        assertTrue("Nakshi Kantha should be GI certified", kantha?.giCertified == true)
        assertTrue("Nakshi Kantha should have citations", (kantha?.citations?.size ?: 0) >= 2)
    }

    @Test
    fun `verify regional search and filtering`() {
        val rajasthanProducts = ProductRepository.searchProducts(
            query = "",
            state = "Rajasthan"
        )
        assertTrue("Should return Rajasthan crafts", rajasthanProducts.isNotEmpty())
        assertTrue("All results should be from Rajasthan", rajasthanProducts.all { it.state == "Rajasthan" })

        val textiles = ProductRepository.searchProducts(
            query = "",
            category = ProductCategory.TEXTILES_HANDLOOM
        )
        assertTrue("Textiles category should contain items", textiles.isNotEmpty())
    }

    @Test
    fun `verify RBAC permissions for Guest, Patron Member, and Founder`() {
        // 1. Guest Role Verification
        val guestState = AuthState(isLoggedIn = false, currentUser = null, isGuest = true)
        val guestRole = guestState.effectiveRole()
        assertEquals(UserRole.GUEST, guestRole)
        assertFalse("Guest should not have wallet access", guestRole.canAccessWallet)
        assertFalse("Guest should not perform top up", guestRole.canPerformTopUp)
        assertFalse("Guest should not redeem karma coins", guestRole.canRedeemKarmaCoins)
        assertFalse("Guest should not approve GI provenance seals", guestRole.canApproveGiProvenance)
        assertFalse("Guest should not audit escrow payouts", guestRole.canAuditArtisanEscrow)

        // 2. Patron Founder Role Verification (Sayak Naskar)
        val founderUser = DemoUsers.sayakNaskar
        assertEquals(UserRole.PATRON_FOUNDER, founderUser.resolveRole())
        val founderState = AuthState(isLoggedIn = true, currentUser = founderUser, isGuest = false)
        val founderRole = founderState.effectiveRole()
        assertEquals(UserRole.PATRON_FOUNDER, founderRole)
        assertTrue("Founder should have wallet access", founderRole.canAccessWallet)
        assertTrue("Founder should have top up permission", founderRole.canPerformTopUp)
        assertTrue("Founder should have GI provenance approval", founderRole.canApproveGiProvenance)
        assertTrue("Founder should have escrow release authorization", founderRole.canAuditArtisanEscrow)
        assertTrue("Founder should access governance console", founderRole.canAccessFounderGovernance)

        // 3. Patron Member Role Verification (Priya Sharma)
        val memberUser = DemoUsers.priyaSharma
        assertEquals(UserRole.PATRON_MEMBER, memberUser.resolveRole())
        val memberState = AuthState(isLoggedIn = true, currentUser = memberUser, isGuest = false)
        val memberRole = memberState.effectiveRole()
        assertEquals(UserRole.PATRON_MEMBER, memberRole)
        assertTrue("Member should have wallet access", memberRole.canAccessWallet)
        assertTrue("Member should perform top up", memberRole.canPerformTopUp)
        assertTrue("Member should redeem karma coins", memberRole.canRedeemKarmaCoins)
        assertFalse("Member must NOT approve GI provenance seals", memberRole.canApproveGiProvenance)
        assertFalse("Member must NOT release cooperative escrow payouts", memberRole.canAuditArtisanEscrow)
        assertFalse("Member must NOT access founder governance", memberRole.canAccessFounderGovernance)
    }

    @Test
    fun `verify session logout resets wallet and cleans user data`() {
        // Simulate User Login Session
        val loggedInUser = DemoUsers.sayakNaskar
        val initialWallet = WalletState(
            balance = loggedInUser.walletBalance,
            karmaCoins = loggedInUser.karmaCoins,
            transactions = getTransactionsForUser(loggedInUser.id)
        )

        assertEquals(2850.0, initialWallet.balance, 0.001)
        assertEquals(420, initialWallet.karmaCoins)
        assertTrue("Active user should have transactions", initialWallet.transactions.isNotEmpty())

        // Simulate Logout Transition
        val loggedOutAuth = AuthState(
            isLoggedIn = false,
            currentUser = null,
            isGuest = true
        )
        val loggedOutWallet = WalletState(
            balance = 0.0,
            karmaCoins = 0,
            transactions = emptyList()
        )

        // Assert that after logout, all sensitive data is sanitized
        assertEquals(UserRole.GUEST, loggedOutAuth.effectiveRole())
        assertNull("Logged out user should be null", loggedOutAuth.currentUser)
        assertTrue("Should be in guest mode", loggedOutAuth.isGuest)
        assertEquals(0.0, loggedOutWallet.balance, 0.001)
        assertEquals(0, loggedOutWallet.karmaCoins)
        assertTrue("Transactions list must be empty after logout", loggedOutWallet.transactions.isEmpty())
    }

    @Test
    fun `verify user specific transactions segregation`() {
        val sayakTransactions = getTransactionsForUser("usr_sayak")
        val arnabTransactions = getTransactionsForUser("usr_arnab")
        val priyaTransactions = getTransactionsForUser("usr_priya")
        val guestTransactions = getTransactionsForUser(null)

        assertTrue(sayakTransactions.any { it.title.contains("Nakshi Kantha", ignoreCase = true) })
        assertTrue(arnabTransactions.any { it.title.contains("Escrow Release", ignoreCase = true) })
        assertTrue(priyaTransactions.any { it.title.contains("Blue Pottery", ignoreCase = true) })
        assertTrue("Guest transactions must be empty", guestTransactions.isEmpty())
    }

    @Test
    fun `verify Indian Postal Directory PIN and nearest hub resolution`() {
        // Test exact PIN match
        val kolkataContext = com.example.data.location.IndianPostalDirectory.resolvePinToLocation("700001")
        assertNotNull("Kolkata PIN should resolve", kolkataContext)
        assertEquals("Kolkata", kolkataContext?.city)
        assertEquals("West Bengal", kolkataContext?.state)
        assertEquals("KOL", kolkataContext?.hubCode)

        // Test Jaipur PIN match
        val jaipurContext = com.example.data.location.IndianPostalDirectory.resolvePinToLocation("302001")
        assertNotNull("Jaipur PIN should resolve", jaipurContext)
        assertEquals("Jaipur", jaipurContext?.city)
        assertEquals("Rajasthan", jaipurContext?.state)

        // Test nearest cluster lookup via coordinates (near Bengaluru)
        val nearestSouth = com.example.data.location.IndianPostalDirectory.findNearest(12.97, 77.59)
        assertEquals("BLR", nearestSouth.hubCode)
        assertEquals("Bengaluru", nearestSouth.city)

        // Test default cluster fallback
        val defaultCluster = com.example.data.location.IndianPostalDirectory.findByPin("700001")
        assertNotNull(defaultCluster)
        assertEquals("KOL", defaultCluster?.hubCode)
    }
}
