package com.cam2cook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// CAM2COOK Samsung Galaxy / One UI palette adapted from the iOS reference.
private val Ink = Color(0xFFF2F2F7)
private val CardInk = Color(0xFFFFFFFF)
private val Primary = Color(0xFF0B6B4F)
private val Teal = Color(0xFF18A874)
private val Cyan = Color(0xFF0B6B4F)
private val TextMain = Color(0xFF0B1410)
private val TextMuted = Color(0xFF5A6861)
private val Amber = Color(0xFF966400)
private val RedSoft = Color(0xFFB4233A)
private val Border = Color(0xFFE3E3E8)
private val Mint = Color(0xFFD1F2DE)
private val PaleGreen = Color(0xFFE8F7EB)
private val WarmPink = Color(0xFFFCA6B0)
private val WarmYellow = Color(0xFFFCE58A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(242, 242, 247)
        window.navigationBarColor = android.graphics.Color.rgb(242, 242, 247)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        setContent { Cam2CookApp() }
    }
}

private enum class Destination { Welcome, Login, SignUp, ForgotPassword, ResetSuccess, Home, Inventory, FoodDetail, Scan, Recognition, Recipes, Cook, ShoppingList, Notifications, Household, Settings, More }

private val LocalAppNavigate = staticCompositionLocalOf<(Destination) -> Unit> { {} }

private data class FoodItem(
    val id: Int,
    val name: String,
    val emoji: String,
    var quantity: String,
    val location: String,
    val daysLeft: Int,
    val confidence: Int = 96,
    var checked: Boolean = true
)

private data class Recipe(
    val name: String,
    val emoji: String,
    val minutes: Int,
    val subtitle: String,
    val ingredients: List<String>,
    val steps: List<String>
)

private val starterFood = listOf(
    FoodItem(1, "Chicken", "🍗", "450 g", "Fridge", 0, 98),
    FoodItem(2, "Spinach", "🥬", "1 bunch", "Fridge", 1, 94),
    FoodItem(3, "Milk", "🥛", "1 L", "Fridge", 1, 99),
    FoodItem(4, "Tomatoes", "🍅", "4 pieces", "Pantry", 4, 97),
    FoodItem(5, "Onion", "🧅", "2 pieces", "Pantry", 8, 93),
    FoodItem(6, "Eggs", "🥚", "6 pieces", "Fridge", 7, 99),
    FoodItem(7, "Cheese", "🧀", "200 g", "Fridge", 5, 96),
    FoodItem(8, "Capsicum", "🫑", "2 pieces", "Fridge", 3, 91)
)

private val recipes = listOf(
    Recipe("20-min Chicken Stir-Fry", "🍗", 20, "Uses 3 items that need attention", listOf("Chicken", "Onion", "Capsicum", "Soy sauce", "Garlic"), listOf("Wash and prepare the vegetables.", "Cut chicken into even, bite-sized pieces.", "Heat a pan and add a little oil.", "Cook chicken thoroughly, stirring regularly.", "Add vegetables and sauce. Cook until tender.", "Serve hot. Check chicken is fully cooked.")),
    Recipe("Spinach & Cheese Omelette", "🍳", 12, "A quick protein-rich breakfast", listOf("Eggs", "Spinach", "Cheese", "Salt", "Pepper"), listOf("Wash and roughly chop the spinach.", "Beat eggs with salt and pepper.", "Wilt spinach in a lightly oiled pan.", "Add eggs and sprinkle cheese.", "Cook until the egg is fully set, then fold.")),
    Recipe("Tomato Garden Pasta", "🍝", 25, "A bright, pantry-friendly meal", listOf("Tomatoes", "Garlic", "Pasta", "Olive oil", "Basil"), listOf("Bring a pot of water to the boil.", "Cook pasta according to the package.", "Chop tomatoes and mince garlic.", "Cook garlic and tomatoes into a quick sauce.", "Toss pasta through the sauce and serve."))
)

@Composable
private fun Cam2CookApp() {
    var destination by remember { mutableStateOf(Destination.Welcome) }
    var selectedRecipe by remember { mutableStateOf(recipes.first()) }
    val inventory = remember { mutableStateListOf<FoodItem>().apply { addAll(starterFood) } }
    var message by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf("Dhruv") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var filter by remember { mutableStateOf("All") }
    var currentStep by remember { mutableStateOf(0) }

    // Device back navigates through the app instead of finishing the activity.
    BackHandler {
        destination = when (destination) {
            Destination.Login, Destination.SignUp, Destination.ForgotPassword -> Destination.Welcome
            Destination.ResetSuccess -> Destination.Login
            Destination.Recognition -> Destination.Scan
            Destination.FoodDetail -> Destination.Inventory
            Destination.ShoppingList, Destination.Notifications, Destination.Household, Destination.Settings -> Destination.More
            Destination.Scan, Destination.Inventory, Destination.Recipes, Destination.More -> Destination.Home
            Destination.Cook -> Destination.Recipes
            Destination.Home, Destination.Welcome -> destination
        }
    }

    MaterialTheme(colorScheme = lightColorScheme(
        primary = Primary, secondary = Teal, background = Ink, surface = CardInk,
        onPrimary = Color.White, onSecondary = Color.White, onBackground = TextMain, onSurface = TextMain
    )) {
        CompositionLocalProvider(LocalAppNavigate provides { target -> destination = target }) {
        Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
            when (destination) {
                Destination.Welcome -> WelcomeScreen(
                    onLogin = { destination = Destination.Login },
                    onSignUp = { destination = Destination.SignUp }
                )
                Destination.Login, Destination.SignUp -> AuthScreen(
                    isSignUp = destination == Destination.SignUp,
                    email = email, onEmail = { email = it },
                    password = password, onPassword = { password = it },
                    name = userName, onName = { userName = it },
                    onBack = { destination = Destination.Welcome },
                    onSubmit = {
                        if (email.contains("@") && password.length >= 8 && (destination != Destination.SignUp || userName.isNotBlank())) {
                            destination = Destination.Home
                            message = "Welcome to your kitchen, ${userName.ifBlank { "chef" }}!"
                        } else message = if (!email.contains("@")) "Enter a valid email address." else "Password must contain at least 8 characters."
                    },
                    message = message,
                    onMessageShown = { message = null },
                    onSwitch = { destination = if (destination == Destination.Login) Destination.SignUp else Destination.Login },
                    onForgotPassword = { destination = Destination.ForgotPassword }
                )
                Destination.ForgotPassword -> UtilityScreen(
                    title = "Reset password", subtitle = "Enter your email and we’ll show the next step.",
                    primaryAction = "Send reset link", onPrimary = { destination = Destination.ResetSuccess },
                    secondaryAction = "Back to sign in", onSecondary = { destination = Destination.Login },
                    fields = listOf("Email address")
                )
                Destination.ResetSuccess -> UtilityScreen(
                    title = "Check your inbox", subtitle = "If an account exists for that email, password-reset instructions will be sent when authentication is connected.",
                    primaryAction = "Back to sign in", onPrimary = { destination = Destination.Login }
                )
                Destination.FoodDetail -> FoodDetailScreen(
                    item = inventory.firstOrNull() ?: starterFood.first(),
                    onBack = { destination = Destination.Inventory },
                    onSave = { message = "Food details saved in this demo session."; destination = Destination.Inventory }
                )
                Destination.ShoppingList -> UtilityScreen(
                    title = "Shopping list", subtitle = "Keep a short list of ingredients to pick up.",
                    primaryAction = "Add sample essentials", onPrimary = { message = "Milk, eggs and fresh greens added to your demo list." },
                    secondaryAction = "Back to home", onSecondary = { destination = Destination.Home },
                    bullets = listOf("Milk", "Eggs", "Fresh greens")
                )
                Destination.Notifications -> UtilityScreen(
                    title = "Notifications", subtitle = "Your kitchen reminders at a glance.",
                    primaryAction = "Mark all as read", onPrimary = { message = "Notifications marked as read." },
                    bullets = listOf("Use spinach today", "Milk is nearing its estimated date", "Your weekly kitchen summary is ready")
                )
                Destination.Household -> UtilityScreen(
                    title = "Your household", subtitle = "Share kitchen planning with people at home.",
                    primaryAction = "Create household", onPrimary = { message = "Household setup is ready to connect to an account service." },
                    secondaryAction = "Back to You", onSecondary = { destination = Destination.More },
                    bullets = listOf("Invite a partner or housemate", "Shared fridge and pantry", "One shopping list for everyone")
                )
                Destination.Settings -> UtilityScreen(
                    title = "Settings", subtitle = "Manage your Cam2Cook experience.",
                    primaryAction = "Save preferences", onPrimary = { message = "Preferences saved for this demo session." },
                    secondaryAction = "Back to You", onSecondary = { destination = Destination.More },
                    bullets = listOf("Freshness reminder preferences", "Measurement units · metric", "Privacy and data controls", "Food safety information")
                )
                Destination.Home -> HomeScreen(inventory,
                    onScan = { destination = Destination.Scan },
                    onInventory = { destination = Destination.Inventory },
                    onRecipes = { destination = Destination.Recipes },
                    onCook = { selectedRecipe = recipes.first(); currentStep = 0; destination = Destination.Cook },
                    onMore = { destination = Destination.More },
                    userName = userName
                )
                Destination.Inventory -> InventoryScreen(inventory, query, { query = it }, filter, { filter = it },
                    onBack = { destination = Destination.Home }, onAdd = { destination = Destination.Scan },
                    onConsume = { item -> inventory.remove(item) },
                    onUpdateQty = { item, qty -> val index = inventory.indexOfFirst { it.id == item.id }; if (index >= 0) inventory[index] = item.copy(quantity = qty) }
                )
                Destination.Scan -> ScanScreen(
                    inventory = inventory,
                    onBack = { destination = Destination.Home },
                    onCapture = { destination = Destination.Recognition }
                )
                Destination.Recognition -> RecognitionScreen(
                    inventory = inventory,
                    onBack = { destination = Destination.Scan },
                    onSave = { inventory.addAll(it.filter { detected -> inventory.none { existing -> existing.name == detected.name } }); destination = Destination.Inventory; message = "Scanned items added to inventory." }
                )
                Destination.Recipes -> RecipesScreen(inventory,
                    onCook = { recipe -> selectedRecipe = recipe; currentStep = 0; destination = Destination.Cook },
                    onBack = { destination = Destination.Home }
                )
                Destination.Cook -> CookScreen(selectedRecipe, currentStep,
                    onBack = { destination = Destination.Recipes },
                    onNext = { if (currentStep < selectedRecipe.steps.lastIndex) currentStep++ else { currentStep = 0; inventory.removeAll { item -> selectedRecipe.ingredients.contains(item.name) && item.name in listOf("Chicken", "Spinach", "Eggs") }; destination = Destination.Home; message = "Recipe complete. Inventory updated for the ingredients marked consumed." } },
                    onPrevious = { currentStep = (currentStep - 1).coerceAtLeast(0) }
                )
                Destination.More -> MoreScreen(userName, email,
                    onBack = { destination = Destination.Home },
                    onLogout = { destination = Destination.Welcome; password = ""; message = null },
                    onShoppingList = { destination = Destination.ShoppingList },
                    onNotifications = { destination = Destination.Notifications },
                    onHousehold = { destination = Destination.Household },
                    onSettings = { destination = Destination.Settings }
                )
            }
            if (message != null && destination != Destination.Login && destination != Destination.SignUp) {
                // Small, dismissible app-level status banner; validation messages stay in the form.
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                    Row(
                        Modifier.fillMaxWidth().padding(16.dp).background(CardInk, RoundedCornerShape(14.dp)).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(message.orEmpty(), color = TextMain, modifier = Modifier.weight(1f), fontSize = 13.sp)
                        IconButton(onClick = { message = null }) { Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMuted) }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun WelcomeScreen(onLogin: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Ink).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(46.dp))
        // Native Compose illustration inspired by the fridge-and-produce source design.
        Box(
            Modifier.fillMaxWidth().height(300.dp).background(Mint, RoundedCornerShape(25.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.padding(16.dp).fillMaxWidth(.66f).background(Color.White, RoundedCornerShape(22.dp)).padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    Modifier.fillMaxWidth().background(PaleGreen, RoundedCornerShape(14.dp)).padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🍅", fontSize = 28.sp)
                    Text("🥬", fontSize = 28.sp)
                    Text("🍋", fontSize = 28.sp)
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    Modifier.fillMaxWidth().background(PaleGreen, RoundedCornerShape(14.dp)).padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("🥛", fontSize = 30.sp)
                    Text("🥚", fontSize = 30.sp)
                    Text("🧀", fontSize = 30.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Surface(color = Color.White, shape = RoundedCornerShape(13.dp)) {
            Text("LESS WASTE, MORE FLAVOUR", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = .6.sp, modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp))
        }
        Spacer(Modifier.height(22.dp))
        Text("Your kitchen, with a little more magic.", color = TextMain, fontSize = 29.sp,
            fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp, textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        Text("Scan what you have. Find what to cook. Feel good about every ingredient.",
            color = TextMuted, fontSize = 15.sp, lineHeight = 21.sp, textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.weight(1f))
        PrimaryButton("Get started", onSignUp)
        TextButton(onClick = onLogin, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Already have an account?  Sign in", color = TextMuted, fontSize = 13.sp)
        }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun AuthScreen(
    isSignUp: Boolean, email: String, onEmail: (String) -> Unit,
    password: String, onPassword: (String) -> Unit, name: String,
    onName: (String) -> Unit, onBack: () -> Unit, onSubmit: () -> Unit,
    message: String?, onMessageShown: () -> Unit, onSwitch: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain) }
        Spacer(Modifier.height(12.dp))
        Text(if (isSignUp) "Create your account" else "Welcome back", color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(if (isSignUp) "Start making more of the food you have." else "Sign in to your kitchen.", color = TextMuted, fontSize = 14.sp)
        Spacer(Modifier.height(28.dp))
        if (isSignUp) {
            OutlinedTextField(value = name, onValueChange = onName, label = { Text("Full name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
        }
        OutlinedTextField(value = email, onValueChange = onEmail, label = { Text("Email address") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = onPassword, label = { Text("Password (8+ characters)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation())
        if (message != null) {
            Spacer(Modifier.height(8.dp))
            Text(message, color = RedSoft, fontSize = 12.sp)
        }
        Spacer(Modifier.height(22.dp))
        PrimaryButton(if (isSignUp) "Create account" else "Login", onSubmit)
        TextButton(onClick = onSwitch, modifier = Modifier.fillMaxWidth()) {
            Text(if (isSignUp) "Already have an account? Login" else "New here? Create an account", color = Teal)
        }
        if (!isSignUp) {
            TextButton(onClick = onForgotPassword, modifier = Modifier.fillMaxWidth()) {
                Text("Forgot password?", color = Teal)
            }
        }
        Text("Authentication connects to the Cam2Cook API when configured.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
    }
}

@Composable
private fun HomeScreen(
    inventory: List<FoodItem>, onScan: () -> Unit, onInventory: () -> Unit,
    onRecipes: () -> Unit, onCook: () -> Unit, onMore: () -> Unit, userName: String
) {
    AppScaffold(Destination.Home, onNavigate = { destination -> when(destination) {
        Destination.Home -> {}; Destination.Inventory -> onInventory(); Destination.Scan -> onScan()
        Destination.Recipes -> onRecipes(); Destination.More -> onMore(); else -> {}
    } }) {
        LazyColumn(Modifier.fillMaxSize().background(Ink).padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Good morning, ${userName.ifBlank { "Chef" }} 👋", color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("Let's make the most of your kitchen.", color = TextMuted, fontSize = 13.sp)
                    }
                    Box(Modifier.size(44.dp).background(Mint, CircleShape), contentAlignment = Alignment.Center) {
                        Text(userName.firstOrNull()?.uppercase() ?: "C", color = Primary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                CardBlock(Modifier.fillMaxWidth(), Primary) {
                    Column(Modifier.padding(18.dp)) {
                        Text("YOUR KITCHEN, AT A GLANCE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Mint, letterSpacing = .7.sp)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("What can we make with what you have?", color = Color.White, fontSize = 21.sp,
                                fontWeight = FontWeight.Bold, lineHeight = 25.sp, modifier = Modifier.weight(1f))
                            Surface(color = Teal, shape = CircleShape, modifier = Modifier.size(44.dp).clickable(onClick = onScan)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.CameraAlt, contentDescription = "Scan ingredients", tint = Color.White) }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Button(onClick = onScan, colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Primary),
                            shape = RoundedCornerShape(13.dp)) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Scan ingredients", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Use these first", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 19.sp, modifier = Modifier.weight(1f))
                    TextButton(onClick = onInventory) { Text("See all", color = Primary) }
                }
                val atRisk = inventory.filter { it.daysLeft <= 1 }.take(3)
                if (atRisk.isEmpty()) {
                    CardBlock(Modifier.fillMaxWidth()) {
                        Text("You're all caught up. Your kitchen is looking fresh!", color = TextMuted, fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    atRisk.forEach { food ->
                        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).background(CardInk, RoundedCornerShape(12.dp)).padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(50.dp).background(if (food.daysLeft <= 0) WarmPink else Mint, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                                Text(food.emoji, fontSize = 25.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(food.name, color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("${deadlineLabel(food.daysLeft)} · ${food.quantity}", color = TextMuted, fontSize = 12.sp)
                            }
                            Surface(color = if (food.daysLeft <= 0) WarmPink else WarmYellow, shape = RoundedCornerShape(13.dp)) {
                                Text(if (food.daysLeft <= 0) "TODAY" else "SOON",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    color = if (food.daysLeft <= 0) Color(0xFF7A1429) else Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text("A smart match for you", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                Spacer(Modifier.height(10.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(78.dp).background(Mint, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) { Text("🍲", fontSize = 36.sp) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Garlic butter chicken", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Uses ${inventory.count { it.daysLeft <= 1 }} items at risk", color = Primary, fontSize = 12.sp)
                            Text("25 min · Easy", color = TextMuted, fontSize = 12.sp)
                            TextButton(onClick = onRecipes, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("View recipe →", color = Primary) }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Freshness is an estimate unless confirmed from a label or user entry.", color = TextMuted, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun InventoryScreen(
    inventory: List<FoodItem>, query: String, onQuery: (String) -> Unit, filter: String,
    onFilter: (String) -> Unit, onBack: () -> Unit, onAdd: () -> Unit,
    onConsume: (FoodItem) -> Unit, onUpdateQty: (FoodItem, String) -> Unit
) {
    AppScaffold(Destination.Inventory, onNavigate = { destination -> when(destination) {
        Destination.Home -> onBack(); Destination.Inventory -> {}; Destination.Scan -> onAdd()
        Destination.Recipes -> {}; Destination.More -> onBack(); else -> {}
    } }) {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Your Inventory", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("${inventory.size} items tracked", color = TextMuted, fontSize = 13.sp)
                    }
                    IconButton(onClick = onAdd, modifier = Modifier.background(Primary, CircleShape)) { Icon(Icons.Default.Add, contentDescription = "Add food", tint = TextMain) }
                }
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search food") }, leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, singleLine = true)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    listOf("All", "Fresh", "Soon", "At risk").forEach { category ->
                        val active = filter == category
                        Surface(color = if (active) Teal.copy(alpha = .18f) else CardInk, shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { onFilter(category) }) {
                            Text(category, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), color = if (active) Teal else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            items(inventory.filter { item ->
                item.name.contains(query, ignoreCase = true) && when(filter) {
                    "Fresh" -> item.daysLeft > 3
                    "Soon" -> item.daysLeft in 2..3
                    "At risk" -> item.daysLeft <= 1
                    else -> true
                }
            }, key = { it.id }) { item ->
                InventoryRow(item, onConsume = { onConsume(item) }, onUpdateQty = { qty -> onUpdateQty(item, qty) })
                Spacer(Modifier.height(10.dp))
            }
            item {
                if (inventory.isEmpty()) EmptyState("Your kitchen is empty", "Scan a photo or add food to get started.", onAdd)
                else Text("Freshness is an estimate. Always check labels and storage guidance.", color = TextMuted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun InventoryRow(item: FoodItem, onConsume: () -> Unit, onUpdateQty: (String) -> Unit) {
    var editing by remember(item.id) { mutableStateOf(false) }
    var quantity by remember(item.id, item.quantity) { mutableStateOf(item.quantity) }
    CardBlock(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Ink, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(44.dp), content = { Box(contentAlignment = Alignment.Center) { Text(item.emoji, fontSize = 24.sp) } })
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.name, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                if (editing) {
                    OutlinedTextField(value = quantity, onValueChange = { quantity = it }, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), singleLine = true, label = { Text("Quantity") })
                    TextButton(onClick = { onUpdateQty(quantity); editing = false }) { Text("Save quantity", color = Teal) }
                } else Text("${item.quantity} • ${item.location}", color = TextMuted, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                val badgeColor = statusColor(item.daysLeft)
                Surface(color = badgeColor.copy(alpha = .14f), shape = RoundedCornerShape(10.dp)) {
                    Text(deadlineLabel(item.daysLeft), modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), color = badgeColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Row {
                    TextButton(onClick = { editing = !editing }) { Text(if (editing) "Cancel" else "Edit", color = Teal, fontSize = 11.sp) }
                    TextButton(onClick = onConsume) { Text("Used", color = TextMuted, fontSize = 11.sp) }
                }
            }
        }
    }
}

@Composable
private fun ScanScreen(inventory: List<FoodItem>, onBack: () -> Unit, onCapture: () -> Unit) {
    AppScaffold(Destination.Scan, onNavigate = { destination -> when(destination) {
        Destination.Home -> onBack(); Destination.Inventory -> onBack(); Destination.Scan -> {}; Destination.Recipes -> onBack(); Destination.More -> onBack(); else -> {}
    } }) {
        Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(Modifier.padding(top = 24.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain) }
                Column {
                    Text("Scan your food", color = TextMain, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("Capture what’s in your fridge or pantry", color = TextMuted, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(16.dp))
            CardBlock(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CAMERA SCAN", color = Teal, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(15.dp))
                    Box(Modifier.fillMaxWidth().height(370.dp).background(Mint, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { Text("🍅", fontSize = 38.sp); Text("🥬", fontSize = 38.sp); Text("🥛", fontSize = 38.sp) }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { Text("🍗", fontSize = 38.sp); Text("🧅", fontSize = 38.sp) }
                            Text("Position the food in the scan frame", color = TextMuted, fontSize = 12.sp)
                        }
                        Box(Modifier.fillMaxSize().padding(20.dp)) {
                            ScanCorner(Modifier.align(Alignment.TopStart))
                            ScanCorner(Modifier.align(Alignment.TopEnd))
                            ScanCorner(Modifier.align(Alignment.BottomStart))
                            ScanCorner(Modifier.align(Alignment.BottomEnd))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Ready to identify ${inventory.size} items already tracked nearby?", color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton("Capture & identify food", onCapture, leadingIcon = { Icon(Icons.Default.CameraAlt, contentDescription = null, tint = TextMain) })
                    Spacer(Modifier.height(6.dp))
                    Text("Demo scan uses sample detections. Connect the vision service for real recognition.", color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun ScanCorner(modifier: Modifier = Modifier) {
    Box(modifier.size(30.dp, 30.dp)) {
        Box(Modifier.align(Alignment.TopStart).width(28.dp).height(3.dp).background(Teal, RoundedCornerShape(2.dp)))
        Box(Modifier.align(Alignment.TopStart).width(3.dp).height(28.dp).background(Teal, RoundedCornerShape(2.dp)))
    }
}

@Composable
private fun RecognitionScreen(inventory: List<FoodItem>, onBack: () -> Unit, onSave: (List<FoodItem>) -> Unit) {
    val detections = remember {
        mutableStateListOf(
            FoodItem(101, "Tomatoes", "🍅", "3 pieces", "Fridge", 4, 96),
            FoodItem(102, "Spinach", "🥬", "1 bunch", "Fridge", 1, 92),
            FoodItem(103, "Milk", "🥛", "1 L", "Fridge", 1, 98),
            FoodItem(104, "Onion", "🧅", "2 pieces", "Pantry", 8, 91)
        )
    }
    AppScaffold(Destination.Inventory, onNavigate = { destination -> if (destination == Destination.Home) onBack() }) {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Text("Review detected food", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("AI found these ingredients. Confirm the list before saving.", color = TextMuted, fontSize = 13.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(20.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧠", fontSize = 48.sp)
                        Text("Recognition preview", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(6.dp).background(Ink, RoundedCornerShape(10.dp))) {
                            Box(Modifier.fillMaxWidth(.72f).height(6.dp).background(Teal, RoundedCornerShape(10.dp)))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Demo analysis complete · sample detections", color = TextMuted, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Detected items", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${detections.count { it.checked }} selected", color = Teal, fontSize = 12.sp)
                }
                Spacer(Modifier.height(10.dp))
            }
            items(detections, key = { it.id }) { item ->
                CardBlock(Modifier.fillMaxWidth().clickable {
                    val index = detections.indexOfFirst { it.id == item.id }
                    if (index >= 0) detections[index] = item.copy(checked = !item.checked)
                }) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = if (item.checked) Teal.copy(alpha = .16f) else Ink, shape = CircleShape, modifier = Modifier.size(34.dp)) {
                            Box(contentAlignment = Alignment.Center) { Icon(if (item.checked) Icons.Default.Check else Icons.Default.Add, contentDescription = if (item.checked) "Selected" else "Not selected", tint = if(item.checked) Teal else TextMuted) }
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(item.emoji, fontSize = 25.sp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(item.name, color = TextMain, fontWeight = FontWeight.SemiBold)
                            Text("${item.quantity} · ${item.location}", color = TextMuted, fontSize = 12.sp)
                        }
                        Text("${item.confidence}%", color = Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            item {
                Spacer(Modifier.height(14.dp))
                PrimaryButton("Save to inventory", { onSave(detections.filter { it.checked }) })
                Text("Visual detection is an estimate; review names, quantities, and dates.", color = TextMuted, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun RecipesScreen(inventory: List<FoodItem>, onCook: (Recipe) -> Unit, onBack: () -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    AppScaffold(Destination.Recipes, onNavigate = { destination -> when(destination) {
        Destination.Home -> onBack(); Destination.Inventory -> onBack(); Destination.Scan -> onBack()
        Destination.Recipes -> {}; Destination.More -> onBack(); else -> {}
    } }) {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Text("Recipes", color = TextMain, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Text("Meals that make the most of your kitchen.", color = TextMuted, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = search, onValueChange = { search = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search recipes") }, leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }, singleLine = true)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All", "Under 20 min", "Use soon").forEach { choice ->
                        Surface(color = if (choice == selectedFilter) Teal.copy(alpha = .16f) else CardInk, shape = RoundedCornerShape(20.dp), modifier = Modifier.clickable { selectedFilter = choice }) {
                            Text(choice, modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp), color = if (choice == selectedFilter) Teal else TextMuted, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            items(recipes.filter { recipe ->
                recipe.name.contains(search, true) && when(selectedFilter) {
                    "Under 20 min" -> recipe.minutes < 20
                    "Use soon" -> recipe.ingredients.any { ingredient -> inventory.any { it.name == ingredient && it.daysLeft <= 1 } }
                    else -> true
                }
            }, key = { it.name }) { recipe ->
                CardBlock(Modifier.fillMaxWidth()) {
                    Column {
                        Box(Modifier.fillMaxWidth().height(112.dp).background(PaleGreen, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), contentAlignment = Alignment.Center) {
                            Text(recipe.emoji, fontSize = 52.sp)
                        }
                        Column(Modifier.padding(14.dp)) {
                            Text(recipe.name, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(5.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = Teal, modifier = Modifier.size(15.dp))
                                Text(" ${recipe.minutes} min  ·  Easy", color = TextMuted, fontSize = 12.sp)
                            }
                            Text(recipe.subtitle, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                            Spacer(Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Ingredients", color = TextMain, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                TextButton(onClick = { onCook(recipe) }) { Text("Cook now →", color = Teal) }
                            }
                            Text(recipe.ingredients.joinToString(" · "), color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            item { if (recipes.isEmpty()) EmptyState("No matching recipes", "Try a different search or filter.", onBack) }
        }
    }
}

@Composable
private fun CookScreen(recipe: Recipe, step: Int, onBack: () -> Unit, onNext: () -> Unit, onPrevious: () -> Unit) {
    var timerSeconds by remember(recipe.name, step) { mutableStateOf(0) }
    var timerRunning by remember(recipe.name, step) { mutableStateOf(false) }
    LaunchedEffect(timerRunning, recipe.name, step) {
        while (timerRunning && timerSeconds > 0) {
            delay(1_000)
            timerSeconds = (timerSeconds - 1).coerceAtLeast(0)
        }
        if (timerSeconds == 0) timerRunning = false
    }
    AppScaffold(null, onNavigate = { destination -> if(destination == Destination.Home) onBack() }) {
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 22.dp, bottom = 22.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain) }
                    Column(Modifier.weight(1f)) {
                        Text("COOK MODE", color = Teal, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        Text(recipe.name, color = TextMain, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(18.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth().height(180.dp).background(Mint, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👨‍🍳", fontSize = 52.sp)
                            Text("Cooking workspace", color = TextMuted, fontSize = 12.sp)
                            Text("Live vision guidance can be connected here", color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Step ${step + 1} of ${recipe.steps.size}", color = Teal, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${((step + 1) * 100 / recipe.steps.size)}% complete", color = TextMuted, fontSize = 11.sp)
                }
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(6.dp).background(CardInk, RoundedCornerShape(10.dp))) {
                    Box(Modifier.fillMaxWidth((step + 1f) / recipe.steps.size).height(6.dp).background(Teal, RoundedCornerShape(10.dp)))
                }
                Spacer(Modifier.height(18.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("CURRENT STEP", color = Amber, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(recipe.steps[step], color = TextMain, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp)
                        Spacer(Modifier.height(14.dp))
                        Text("Safety tip: follow package instructions and verify safe internal temperature with a food thermometer when needed.", color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Teal)
                            Spacer(Modifier.width(8.dp))
                            Text("Step timer", color = TextMain, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("${timerSeconds / 60}:${(timerSeconds % 60).toString().padStart(2, '0')}", color = Teal, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(60, 180, 300).forEach { seconds ->
                                TextButton(onClick = { timerSeconds = seconds; timerRunning = false }) { Text("${seconds / 60} min", color = Teal) }
                            }
                            TextButton(onClick = { timerRunning = !timerRunning && timerSeconds > 0 }) { Text(if (timerRunning) "Pause" else "Start", color = Teal) }
                            TextButton(onClick = { timerRunning = false; timerSeconds = 0 }) { Text("Reset", color = TextMuted) }
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SecondaryButton("Previous", onPrevious, Modifier.weight(1f), enabled = step > 0)
                    PrimaryButton(if (step == recipe.steps.lastIndex) "Finish cooking" else "Complete step", onNext, Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Text("Talk to your cooking companion", color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Voice and live-camera guidance are integration points; this build provides a step-aware recipe flow.", color = TextMuted, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

@Composable
private fun MoreScreen(name: String, email: String, onBack: () -> Unit, onLogout: () -> Unit,
    onShoppingList: () -> Unit, onNotifications: () -> Unit, onHousehold: () -> Unit, onSettings: () -> Unit) {
    AppScaffold(Destination.More, onNavigate = { if (it == Destination.Home) onBack() }) {
        Column(Modifier.fillMaxSize().padding(24.dp)) {
            Text("More", color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(20.dp))
            CardBlock(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("👨‍🍳  $name", color = TextMain, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(email.ifBlank { "Account not connected" }, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            CardBlock(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Food safety", color = TextMain, fontWeight = FontWeight.Bold)
                    Text("Freshness dates shown here are estimates unless supplied from a package label or user entry.", color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp, modifier = Modifier.padding(top = 5.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            listOf(
                "Shopping list" to onShoppingList,
                "Notifications" to onNotifications,
                "Household sharing" to onHousehold,
                "Settings & preferences" to onSettings
            ).forEach { (label, action) ->
                CardBlock(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(Modifier.fillMaxWidth().clickable(onClick = action).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(label, color = TextMain, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("›", color = Primary, fontSize = 22.sp)
                    }
                }
            }
            SecondaryButton("Back to home", onBack)
            Spacer(Modifier.height(10.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = CardInk, contentColor = RedSoft), shape = RoundedCornerShape(14.dp)) { Text("Log out") }
        }
    }
}


@Composable
private fun UtilityScreen(
    title: String, subtitle: String, primaryAction: String, onPrimary: () -> Unit,
    onBack: () -> Unit = { },
    secondaryAction: String? = null, onSecondary: (() -> Unit)? = null,
    bullets: List<String> = emptyList(), fields: List<String> = emptyList()
) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Top) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain)
        }
        Spacer(Modifier.height(18.dp))
        Text(title, color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, color = TextMuted, fontSize = 14.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(24.dp))
        fields.forEach { field ->
            var value by remember(field) { mutableStateOf("") }
            OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text(field) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                keyboardOptions = if (field.contains("email", true)) KeyboardOptions(keyboardType = KeyboardType.Email) else KeyboardOptions.Default)
            Spacer(Modifier.height(12.dp))
        }
        bullets.forEach { bullet ->
            CardBlock(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("•", color = Primary, fontSize = 22.sp)
                    Spacer(Modifier.width(12.dp))
                    Text(bullet, color = TextMain, fontSize = 14.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(primaryAction, onPrimary)
        if (secondaryAction != null && onSecondary != null) {
            Spacer(Modifier.height(8.dp))
            SecondaryButton(secondaryAction, onSecondary)
        }
    }
}

@Composable
private fun FoodDetailScreen(item: FoodItem, onBack: () -> Unit, onSave: () -> Unit) {
    var quantity by remember(item.id) { mutableStateOf(item.quantity) }
    var location by remember(item.id) { mutableStateOf(item.location) }
    Column(Modifier.fillMaxSize().padding(24.dp)) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextMain) }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().height(160.dp).background(Mint, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
            Text(item.emoji, fontSize = 76.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(item.name, color = TextMain, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(deadlineLabel(item.daysLeft), color = statusColor(item.daysLeft), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantity") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Storage location") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))
        Text("Freshness dates are estimates, not a food-safety guarantee. Check packaging and use your judgement.", color = TextMuted, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.weight(1f))
        PrimaryButton("Save changes", onSave)
    }
}

@Composable
private fun AppScaffold(
    current: Destination?,
    onNavigate: (Destination) -> Unit,
    content: @Composable () -> Unit
) {
    val navigate = LocalAppNavigate.current
    Scaffold(
        containerColor = Ink,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (current != null) {
                Column(Modifier.fillMaxWidth().background(Ink).windowInsetsPadding(WindowInsets.navigationBars)) {
                    Divider(color = Border, thickness = 1.dp)
                    Row(Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                        NavItem("Home", "⌂", current == Destination.Home) { navigate(Destination.Home) }
                        NavItem("Fridge", "▦", current == Destination.Inventory) { navigate(Destination.Inventory) }
                        NavItem("Recipes", "◉", current == Destination.Recipes) { navigate(Destination.Recipes) }
                        NavItem("List", "☷", false) { navigate(Destination.Inventory) }
                        NavItem("You", "●", current == Destination.More) { navigate(Destination.More) }
                    }
                }
            }
        }
    ) { padding -> Box(Modifier.fillMaxSize().padding(padding)) { content() } }
}

@Composable
private fun NavItem(label: String, icon: String, active: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(64.dp).clickable(onClick = onClick).padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, color = if (active) Primary else TextMuted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = if (active) Primary else TextMuted, fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun QuickAction(emoji: String, title: String, description: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    CardBlock(modifier.height(116.dp).clickable(onClick = onClick)) {
        Column(Modifier.fillMaxSize().padding(horizontal = 5.dp, vertical = 14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 25.sp)
            Spacer(Modifier.height(8.dp))
            Text(title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(description, color = TextMuted, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 12.sp)
        }
    }
}

@Composable
private fun Stat(value: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.width(5.dp))
        Text(label, color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun CardBlock(modifier: Modifier = Modifier, color: Color = CardInk, content: @Composable () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        colors = CardDefaults.cardColors(containerColor = color),
        content = { content() }
    )
}

@Composable
private fun PrimaryButton(
    label: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled, shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = Color.White, disabledContainerColor = CardInk, disabledContentColor = TextMuted)
    ) {
        if (leadingIcon != null) { leadingIcon(); Spacer(Modifier.width(8.dp)) }
        Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SecondaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled, shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = CardInk, contentColor = Teal, disabledContainerColor = CardInk.copy(alpha = .5f), disabledContentColor = TextMuted)
    ) { Text(label, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun EmptyState(title: String, detail: String, onAction: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🥗", fontSize = 40.sp)
        Spacer(Modifier.height(10.dp))
        Text(title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(detail, color = TextMuted, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
        PrimaryButton("Scan food", onAction)
    }
}

internal fun statusColor(daysLeft: Int): Color = when {
    daysLeft <= 0 -> RedSoft
    daysLeft <= 1 -> Amber
    daysLeft <= 3 -> Teal
    else -> Teal
}

internal fun deadlineLabel(daysLeft: Int): String = when {
    daysLeft < 0 -> "Review date"
    daysLeft == 0 -> "Today"
    daysLeft == 1 -> "1 day left"
    else -> "${daysLeft} days"
}
