package com.cam2cook

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

private val Ink = Color(0xFF090E17)
private val CardInk = Color(0xFF172138)
private val Primary = Color(0xFFFF0040)
private val Teal = Color(0xFF4ECDC4)
private val Cyan = Color(0xFF00BBFF)
private val TextMain = Color(0xFFF8FAFC)
private val TextMuted = Color(0xFFCBD5E0)
private val Amber = Color(0xFFFFB047)
private val RedSoft = Color(0xFFFF6B6B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { Cam2CookApp() }
    }
}

private enum class Destination { Welcome, Login, SignUp, Home, Inventory, Scan, Recognition, Recipes, Cook, More }

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

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Primary, secondary = Teal, background = Ink, surface = CardInk,
        onPrimary = TextMain, onSecondary = Ink, onBackground = TextMain, onSurface = TextMain
    )) {
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
                    onSwitch = { destination = if (destination == Destination.Login) Destination.SignUp else Destination.Login }
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
                    onLogout = { destination = Destination.Welcome; password = ""; message = null }
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

@Composable
private fun WelcomeScreen(onLogin: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(Ink).padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(110.dp))
        Box(Modifier.size(124.dp).background(Primary, CircleShape), contentAlignment = Alignment.Center) { Text("🍳", fontSize = 58.sp) }
        Spacer(Modifier.height(28.dp))
        Text("CAM2COOK", color = TextMain, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
        Spacer(Modifier.height(14.dp))
        Text("See what you have.\nKnow when to use it.", color = TextMain, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 30.sp)
        Spacer(Modifier.height(14.dp))
        Text("Scan your fridge or groceries and let AI turn what you already have into your next meal.", color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center, lineHeight = 21.sp)
        Spacer(Modifier.weight(1f))
        PrimaryButton("Login", onLogin)
        Spacer(Modifier.height(14.dp))
        SecondaryButton("Sign Up", onSignUp)
        Spacer(Modifier.height(34.dp))
        Text("Your estimates are editable and never replace package labels or food-safety guidance.", color = TextMuted, fontSize = 10.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun AuthScreen(
    isSignUp: Boolean, email: String, onEmail: (String) -> Unit,
    password: String, onPassword: (String) -> Unit, name: String,
    onName: (String) -> Unit, onBack: () -> Unit, onSubmit: () -> Unit,
    message: String?, onMessageShown: () -> Unit, onSwitch: () -> Unit
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
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 20.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 28.dp, bottom = 22.dp)) {
            item {
                Text("Good evening 👋", color = TextMuted, fontSize = 14.sp)
                Spacer(Modifier.height(7.dp))
                Text("Attention!", color = TextMain, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(14.dp))
                CardBlock(Modifier.fillMaxWidth(), Primary) {
                    Column(Modifier.padding(16.dp)) {
                        Text("USE SOON", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMain, letterSpacing = 1.sp)
                        Text("${inventory.count { it.daysLeft <= 1 }} items need attention", color = TextMain, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Text(inventory.filter { it.daysLeft <= 1 }.joinToString(" • ") { it.name }.ifEmpty { "You're all caught up!" }, color = TextMain, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Text("Quick actions", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                    QuickAction("📷", "Scan food", "Add from camera", onScan, Modifier.weight(1f))
                    QuickAction("🍳", "Cook", "Start with your food", onCook, Modifier.weight(1f))
                    QuickAction("🛒", "Shopping", "What’s missing", onInventory, Modifier.weight(1f))
                }
                Spacer(Modifier.height(20.dp))
                Text("Your kitchen", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${inventory.size}", color = Teal, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(8.dp))
                            Text("items tracked", color = TextMuted, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Stat("${inventory.count { it.daysLeft > 3 }}", "fresh", TextMain)
                            Stat("${inventory.count { it.daysLeft in 2..3 }}", "use soon", Amber)
                            Stat("${inventory.count { it.daysLeft <= 1 }}", "at risk", RedSoft)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text("Recommended for you", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))
                CardBlock(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🍗", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("20-min Chicken Stir-Fry", color = TextMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Uses food that needs attention", color = TextMuted, fontSize = 11.sp)
                        }
                        Button(onClick = onCook, colors = ButtonDefaults.buttonColors(containerColor = Cyan, contentColor = Ink), contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)) { Text("Cook", fontSize = 12.sp) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Tip: tap an item to edit quantity, dates, or storage.", color = TextMuted, fontSize = 11.sp)
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
                    Box(Modifier.fillMaxWidth().height(370.dp).background(Color(0xFF111B2C), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
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
        listOf(
            FoodItem(101, "Tomatoes", "🍅", "3 pieces", "Fridge", 4, 96),
            FoodItem(102, "Spinach", "🥬", "1 bunch", "Fridge", 1, 92),
            FoodItem(103, "Milk", "🥛", "1 L", "Fridge", 1, 98),
            FoodItem(104, "Onion", "🧅", "2 pieces", "Pantry", 8, 91)
        ).toMutableStateList()
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
                CardBlock(Modifier.fillMaxWidth().clickable { item.checked = !item.checked }) {
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
                        Box(Modifier.fillMaxWidth().height(112.dp).background(Color(0xFF202B40), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), contentAlignment = Alignment.Center) {
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
                    Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFF111B2C), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
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
private fun MoreScreen(name: String, email: String, onBack: () -> Unit, onLogout: () -> Unit) {
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
            SecondaryButton("Back to home", onBack)
            Spacer(Modifier.height(10.dp))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = CardInk, contentColor = RedSoft), shape = RoundedCornerShape(14.dp)) { Text("Log out") }
        }
    }
}

@Composable
private fun AppScaffold(
    current: Destination?,
    onNavigate: (Destination) -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = Ink,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (current != null) {
                Column(Modifier.fillMaxWidth().background(Ink).windowInsetsPadding(WindowInsets.navigationBars)) {
                    Divider(color = Color(0xFF29384F), thickness = 1.dp)
                    Row(Modifier.fillMaxWidth().height(58.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                        NavItem("Home", "⌂", current == Destination.Home) { onNavigate(Destination.Home) }
                        NavItem("Inventory", "▦", current == Destination.Inventory) { onNavigate(Destination.Inventory) }
                        NavItem("Scan", "＋", current == Destination.Scan) { onNavigate(Destination.Scan) }
                        NavItem("Recipes", "◉", current == Destination.Recipes) { onNavigate(Destination.Recipes) }
                        NavItem("More", "☰", current == Destination.More) { onNavigate(Destination.More) }
                    }
                }
            }
        }
    ) { padding -> Box(Modifier.fillMaxSize().padding(padding)) { content() } }
}

@Composable
private fun NavItem(label: String, icon: String, active: Boolean, onClick: () -> Unit) {
    Column(Modifier.width(64.dp).clickable(onClick = onClick).padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, color = if (active) RedSoft else TextMuted, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = if (active) RedSoft else TextMuted, fontSize = 9.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
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
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color), content = { content() })
}

@Composable
private fun PrimaryButton(
    label: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, leadingIcon: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick, modifier = modifier.fillMaxWidth().height(52.dp),
        enabled = enabled, shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = TextMain, disabledContainerColor = CardInk, disabledContentColor = TextMuted)
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
