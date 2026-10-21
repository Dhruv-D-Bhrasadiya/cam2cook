import SwiftUI
import PhotosUI

private enum Palette {
    static let background = Color(red: 0.055, green: 0.067, blue: 0.063)
    static let surface = Color(red: 0.105, green: 0.125, blue: 0.115)
    static let muted = Color(red: 0.59, green: 0.64, blue: 0.60)
    static let lime = Color(red: 0.77, green: 0.96, blue: 0.36)
    static let line = Color.white.opacity(0.08)
}

struct MainTabView: View {
    @State private var selected = 0
    var body: some View {
        TabView(selection: $selected) {
            HomeView().tabItem { Label("Home", systemImage: "house.fill") }.tag(0)
            InventoryView().tabItem { Label("Inventory", systemImage: "square.grid.2x2.fill") }.tag(1)
            RecipesView().tabItem { Label("Recipes", systemImage: "book.closed.fill") }.tag(2)
            ProfileView().tabItem { Label("You", systemImage: "person.crop.circle.fill") }.tag(3)
        }
        .tint(Palette.lime)
        .toolbarBackground(Palette.background, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
    }
}

struct HomeView: View {
    @EnvironmentObject private var store: InventoryStore
    @State private var showScanner = false
    private var urgent: [FoodItem] { store.items.filter { $0.daysLeft <= 2 }.sorted { $0.daysLeft < $1.daysLeft } }

    var body: some View {
        NavigationStack {
            ScrollView(showsIndicators: false) {
                VStack(alignment: .leading, spacing: 26) {
                    header
                    greeting
                    scanCard
                    HStack(spacing: 12) {
                        MetricCard(value: "\(store.items.count)", label: "Items tracked", icon: "leaf.fill")
                        MetricCard(value: "\(urgent.count)", label: "Need attention", icon: "clock.fill", accent: true)
                    }
                    sectionTitle("Use these first", action: "See all") { }
                    if urgent.isEmpty {
                        Text("You're all caught up. Nice work keeping food fresh!").font(.subheadline).foregroundStyle(Palette.muted)
                    } else {
                        ForEach(urgent.prefix(3)) { item in FoodRow(item: item) }
                    }
                    recipeFeature
                    Spacer(minLength: 18)
                }.padding(.horizontal, 20).padding(.top, 10).padding(.bottom, 24)
            }
            .background(Palette.background.ignoresSafeArea())
            .toolbar(.hidden, for: .navigationBar)
            .sheet(isPresented: $showScanner) { ScanView().environmentObject(store).presentationDetents([.large]).presentationDragIndicator(.visible) }
        }
    }

    private var header: some View {
        HStack(spacing: 10) {
            ZStack { RoundedRectangle(cornerRadius: 12).fill(Palette.lime).frame(width: 42, height: 42); Image(systemName: "camera.macro").font(.system(size: 20, weight: .bold)).foregroundStyle(Palette.background) }
            VStack(alignment: .leading, spacing: 1) { Text("cam2cook").font(.system(size: 20, weight: .bold, design: .rounded)); Text("LESS WASTE. MORE TASTE.").font(.system(size: 9, weight: .bold, design: .rounded)).tracking(1.4).foregroundStyle(Palette.muted) }
            Spacer()
            Button {} label: { Image(systemName: "bell.badge").font(.system(size: 19)).foregroundStyle(.white).frame(width: 44, height: 44).background(Palette.surface, in: Circle()) }
        }
    }
    private var greeting: some View {
        VStack(alignment: .leading, spacing: 7) {
            Text("TUESDAY, OCTOBER 21").font(.system(size: 11, weight: .bold)).tracking(1.5).foregroundStyle(Palette.lime)
            Text("Your kitchen,\n").foregroundStyle(.white) + Text("in good hands.").foregroundStyle(Palette.lime)
            Text("Here's what's happening with your food today.").font(.subheadline).foregroundStyle(Palette.muted).padding(.top, 2)
        }.font(.system(size: 33, weight: .bold, design: .rounded))
    }
    private var scanCard: some View {
        Button { showScanner = true } label: {
            ZStack(alignment: .bottomLeading) {
                RoundedRectangle(cornerRadius: 24).fill(LinearGradient(colors: [Color(red: 0.20, green: 0.28, blue: 0.19), Color(red: 0.12, green: 0.17, blue: 0.13)], startPoint: .topLeading, endPoint: .bottomTrailing))
                Circle().fill(Palette.lime.opacity(0.12)).frame(width: 180).blur(radius: 1).offset(x: 210, y: -55)
                VStack(alignment: .leading, spacing: 12) {
                    HStack { Image(systemName: "viewfinder").font(.system(size: 24)); Spacer(); Text("AI POWERED").font(.system(size: 10, weight: .bold)).tracking(1.2).padding(.horizontal, 10).padding(.vertical, 7).background(.white.opacity(0.09), in: Capsule()) }
                    Spacer()
                    Text("Take a fresh look.").font(.system(size: 24, weight: .bold, design: .rounded))
                    Text("Scan your groceries and let us handle the rest.").font(.subheadline).foregroundStyle(.white.opacity(0.72))
                    HStack { Text("Scan my food"); Image(systemName: "arrow.up.right").font(.system(size: 12, weight: .bold)) }.font(.system(size: 14, weight: .bold)).foregroundStyle(Palette.background).padding(.horizontal, 16).padding(.vertical, 12).background(Palette.lime, in: Capsule()).padding(.top, 5)
                }.foregroundStyle(.white).padding(20)
            }.frame(height: 220).clipShape(RoundedRectangle(cornerRadius: 24))
        }.buttonStyle(.plain)
    }
    private var recipeFeature: some View {
        VStack(alignment: .leading, spacing: 12) {
            sectionTitle("Today's inspiration", action: "Explore") {}
            HStack(spacing: 14) {
                ZStack { RoundedRectangle(cornerRadius: 16).fill(Color(red: 0.24, green: 0.20, blue: 0.14)); Text("🥑").font(.system(size: 43)); Text("🍳").font(.system(size: 26)).offset(x: 24, y: 20) }.frame(width: 100, height: 110)
                VStack(alignment: .leading, spacing: 7) {
                    Text("WASTE LESS, EAT WELL").font(.system(size: 9, weight: .bold)).tracking(1).foregroundStyle(Palette.lime)
                    Text("Green goddess toast").font(.system(size: 17, weight: .bold, design: .rounded))
                    Text("Uses your avocado · 10 min").font(.caption).foregroundStyle(Palette.muted)
                    HStack(spacing: 5) { Image(systemName: "sparkles"); Text("Great match") }.font(.system(size: 11, weight: .semibold)).foregroundStyle(Palette.lime)
                }
                Spacer(minLength: 0)
            }.padding(12).background(Palette.surface, in: RoundedRectangle(cornerRadius: 20))
        }
    }
}

struct InventoryView: View {
    @EnvironmentObject private var store: InventoryStore
    @State private var search = ""
    @State private var filter = "All"
    @State private var showAdd = false
    private let filters = ["All", "Use soon", "Fresh"]
    private var visible: [FoodItem] { store.items.filter { ($0.name.localizedCaseInsensitiveContains(search) || search.isEmpty) && (filter == "All" || (filter == "Use soon" ? $0.daysLeft <= 2 : $0.daysLeft > 2)) } }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 18) {
                    HStack { VStack(alignment: .leading, spacing: 5) { Text("Your inventory").font(.system(size: 30, weight: .bold, design: .rounded)); Text("\(store.items.count) items keeping track of freshness").font(.subheadline).foregroundStyle(Palette.muted) }; Spacer(); Button { showAdd = true } label: { Image(systemName: "plus").font(.system(size: 16, weight: .bold)).foregroundStyle(Palette.background).frame(width: 44, height: 44).background(Palette.lime, in: Circle()) } }
                    HStack { Image(systemName: "magnifyingglass").foregroundStyle(Palette.muted); TextField("Search your food", text: $search).tint(Palette.lime) }.padding(14).background(Palette.surface, in: RoundedRectangle(cornerRadius: 14))
                    HStack(spacing: 8) { ForEach(filters, id: \.self) { f in Button { filter = f } label: { Text(f).font(.subheadline.weight(.semibold)).foregroundStyle(filter == f ? Palette.background : Palette.muted).padding(.horizontal, 16).padding(.vertical, 10).background(filter == f ? Palette.lime : Palette.surface, in: Capsule()) } } }
                    HStack { Text("FOOD ITEMS").font(.system(size: 10, weight: .bold)).tracking(1.4).foregroundStyle(Palette.muted); Spacer(); Image(systemName: "slider.horizontal.3").foregroundStyle(Palette.muted) }
                    ForEach(visible) { item in FoodRow(item: item, showActions: true) }
                    if visible.isEmpty { ContentUnavailableView("No food found", systemImage: "leaf", description: Text("Try another search or add an item.")) }
                }.padding(20)
            }.background(Palette.background.ignoresSafeArea()).toolbar(.hidden, for: .navigationBar)
                .sheet(isPresented: $showAdd) { AddFoodView().environmentObject(store).presentationDetents([.medium, .large]).presentationDragIndicator(.visible) }
        }
    }
}

struct FoodRow: View {
    @EnvironmentObject private var store: InventoryStore
    let item: FoodItem
    var showActions = false
    var body: some View {
        HStack(spacing: 13) {
            ZStack { RoundedRectangle(cornerRadius: 15).fill(Color.white.opacity(0.055)); Text(item.symbol).font(.system(size: 30)) }.frame(width: 58, height: 62)
            VStack(alignment: .leading, spacing: 5) {
                Text(item.name).font(.system(size: 15, weight: .semibold))
                Text("\(item.quantity) · \(item.location)").font(.caption).foregroundStyle(Palette.muted)
                HStack(spacing: 4) { Circle().fill(item.daysLeft <= 1 ? Color.orange : Palette.lime).frame(width: 5, height: 5); Text(item.daysLeft < 0 ? "Past estimate · review" : item.daysLeft == 0 ? "Deadline today" : "Best used in \(item.daysLeft) day\(item.daysLeft == 1 ? "" : "s")").font(.system(size: 11, weight: .medium)).foregroundStyle(item.daysLeft <= 1 ? Color.orange : Palette.muted) }
            }
            Spacer(minLength: 0)
            if showActions { Button { store.consume(item) } label: { Image(systemName: "checkmark.circle").font(.system(size: 20)).foregroundStyle(Palette.lime) } }
            else { Image(systemName: "chevron.right").font(.system(size: 11, weight: .bold)).foregroundStyle(Palette.muted) }
        }.padding(12).background(Palette.surface, in: RoundedRectangle(cornerRadius: 18))
    }
}

struct MetricCard: View {
    let value: String
    let label: String
    let icon: String
    var accent = false
    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Image(systemName: icon).font(.system(size: 17, weight: .semibold)).foregroundStyle(accent ? Palette.lime : Palette.muted)
            Text(value).font(.system(size: 29, weight: .bold, design: .rounded))
            Text(label).font(.caption).foregroundStyle(Palette.muted)
        }.frame(maxWidth: .infinity, alignment: .leading).padding(17).background(Palette.surface, in: RoundedRectangle(cornerRadius: 20))
    }
}

struct ScanView: View {
    @EnvironmentObject private var store: InventoryStore
    @Environment(\.dismiss) private var dismiss
    @State private var pickerItem: PhotosPickerItem?
    @State private var image: UIImage?
    @State private var scanned = false
    var body: some View {
        NavigationStack {
            VStack(spacing: 22) {
                ZStack {
                    RoundedRectangle(cornerRadius: 28).fill(Palette.surface)
                    if let image { Image(uiImage: image).resizable().scaledToFit().clipShape(RoundedRectangle(cornerRadius: 24)).padding(8) }
                    else { VStack(spacing: 14) { Image(systemName: "viewfinder").font(.system(size: 52, weight: .light)).foregroundStyle(Palette.lime); Text("See what's in your kitchen").font(.system(size: 21, weight: .bold, design: .rounded)); Text("Choose a photo to start your scan").font(.subheadline).foregroundStyle(Palette.muted) } }
                }.frame(height: 330)
                if scanned {
                    VStack(alignment: .leading, spacing: 10) { Label("Scan preview", systemImage: "sparkles").font(.headline); Text("Demo mode: AI recognition isn't connected yet. You can add a sample item or continue with manual inventory.").font(.subheadline).foregroundStyle(Palette.muted) }.frame(maxWidth: .infinity, alignment: .leading).padding(16).background(Palette.surface, in: RoundedRectangle(cornerRadius: 16))
                }
                PhotosPicker(selection: $pickerItem, matching: .images) { Label("Choose a photo", systemImage: "photo.on.rectangle.angled").frame(maxWidth: .infinity).padding(16).background(Palette.lime, in: Capsule()).foregroundStyle(Palette.background).fontWeight(.bold) }
                .onChange(of: pickerItem) { _, item in Task { if let data = try? await item?.loadTransferable(type: Data.self), let uiImage = UIImage(data: data) { image = uiImage; scanned = true } } }
                Button { store.add(FoodItem(name: "Newly scanned item", category: "Other", quantity: "1 item", location: "Fridge", daysLeft: 3, symbol: "🥦", tint: "green")); dismiss() } label: { Text("Add sample item to inventory").fontWeight(.semibold).foregroundStyle(Palette.lime).padding(.vertical, 8) }
                Spacer()
            }.padding(20).background(Palette.background.ignoresSafeArea()).navigationTitle("Scan your food").navigationBarTitleDisplayMode(.inline).toolbar { ToolbarItem(placement: .topBarTrailing) { Button("Done") { dismiss() }.foregroundStyle(Palette.lime) } }
        }.preferredColorScheme(.dark)
    }
}

struct AddFoodView: View {
    @EnvironmentObject private var store: InventoryStore
    @Environment(\.dismiss) private var dismiss
    @State private var name = ""
    @State private var quantity = "1 item"
    @State private var location = "Fridge"
    @State private var days = 3.0
    var body: some View {
        NavigationStack {
            Form {
                Section("Food details") {
                    TextField("Food name", text: $name)
                    TextField("Quantity", text: $quantity)
                    Picker("Storage", selection: $location) { Text("Fridge").tag("Fridge"); Text("Freezer").tag("Freezer"); Text("Pantry").tag("Pantry") }
                    VStack(alignment: .leading) { Text("Estimated days remaining: \(Int(days))"); Slider(value: $days, in: 0...14, step: 1).tint(Palette.lime) }
                }
                Section { Text("Freshness is an estimate, not a food-safety guarantee. Check packaging and use your judgement.").font(.footnote).foregroundStyle(Palette.muted) }
            }.scrollContentBackground(.hidden).background(Palette.background).navigationTitle("Add food").navigationBarTitleDisplayMode(.inline)
                .toolbar { ToolbarItem(placement: .cancellationAction) { Button("Cancel") { dismiss() }.foregroundStyle(Palette.muted) }; ToolbarItem(placement: .confirmationAction) { Button("Save") { guard !name.trimmingCharacters(in: .whitespaces).isEmpty else { return }; store.add(FoodItem(name: name, category: "Other", quantity: quantity, location: location, daysLeft: Int(days), symbol: "🥗", tint: "green")); dismiss() }.foregroundStyle(Palette.lime).disabled(name.trimmingCharacters(in: .whitespaces).isEmpty) } }
        }.preferredColorScheme(.dark)
    }
}

struct RecipesView: View {
    private let recipes: [(String, String, String, String)] = [
        ("🥑", "Green goddess toast", "10 min · Easy", "Uses avocado and sourdough"),
        ("🍅", "Roasted tomato pasta", "25 min · Easy", "Uses cherry tomatoes"),
        ("🥬", "Spinach & egg skillet", "15 min · Easy", "Uses spinach and eggs"),
        ("🥣", "Zero-waste breakfast bowl", "12 min · Easy", "Flexible pantry ingredients")
    ]
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 6) { Text("Cook something good.").font(.system(size: 30, weight: .bold, design: .rounded)); Text("Ideas based on food you already have.").foregroundStyle(Palette.muted) }
                    HStack(spacing: 12) { Image(systemName: "sparkles").font(.title2).foregroundStyle(Palette.lime); VStack(alignment: .leading, spacing: 4) { Text("Your ingredients, reimagined").font(.headline); Text("Prioritize food that needs using first.").font(.caption).foregroundStyle(Palette.muted) } }.padding(16).frame(maxWidth: .infinity, alignment: .leading).background(Palette.surface, in: RoundedRectangle(cornerRadius: 18))
                    ForEach(recipes, id: \.1) { recipe in
                        HStack(spacing: 14) { Text(recipe.0).font(.system(size: 42)).frame(width: 76, height: 82).background(Palette.surface, in: RoundedRectangle(cornerRadius: 16)); VStack(alignment: .leading, spacing: 6) { Text(recipe.1).font(.system(size: 16, weight: .bold)); Text(recipe.2).font(.caption).foregroundStyle(Palette.lime); Text(recipe.3).font(.caption).foregroundStyle(Palette.muted) }; Spacer(); Image(systemName: "arrow.up.right").foregroundStyle(Palette.muted) }.padding(12).background(Palette.surface.opacity(0.6), in: RoundedRectangle(cornerRadius: 18))
                    }
                    Text("Recipe suggestions are sample content. Connect the recipe service to personalize recommendations.").font(.footnote).foregroundStyle(Palette.muted)
                }.padding(20)
            }.background(Palette.background.ignoresSafeArea()).toolbar(.hidden, for: .navigationBar)
        }
    }
}

struct ProfileView: View {
    @State private var notifications = true
    @State private var household = "My kitchen"
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 20) {
                    VStack(spacing: 12) { ZStack { Circle().fill(Palette.lime).frame(width: 82, height: 82); Image(systemName: "person.fill").font(.system(size: 32)).foregroundStyle(Palette.background) }; Text("Your kitchen profile").font(.system(size: 24, weight: .bold, design: .rounded)); Text("A little less waste, every day.").foregroundStyle(Palette.muted) }.padding(.vertical, 18)
                    VStack(spacing: 0) {
                        settingsRow("house.fill", "Kitchen space", trailing: household)
                        Divider().overlay(Palette.line)
                        Toggle(isOn: $notifications) { Label("Freshness reminders", systemImage: "bell.badge") }.tint(Palette.lime).padding(16)
                        Divider().overlay(Palette.line)
                        settingsRow("lock.shield", "Privacy & data", trailing: " ")
                        Divider().overlay(Palette.line)
                        settingsRow("questionmark.circle", "Help & feedback", trailing: " ")
                    }.background(Palette.surface, in: RoundedRectangle(cornerRadius: 20))
                    Text("CAM2COOK · iOS preview\nFreshness estimates are not food-safety guarantees.").font(.caption).foregroundStyle(Palette.muted).multilineTextAlignment(.center)
                }.padding(20)
            }.background(Palette.background.ignoresSafeArea()).toolbar(.hidden, for: .navigationBar)
        }
    }
    private func settingsRow(_ icon: String, _ title: String, trailing: String) -> some View {
        HStack { Label(title, systemImage: icon); Spacer(); Text(trailing).font(.caption).foregroundStyle(Palette.muted); Image(systemName: "chevron.right").font(.caption).foregroundStyle(Palette.muted) }.padding(16)
    }
}

private func sectionTitle(_ title: String, action: String, onTap: @escaping () -> Void) -> some View {
    HStack { Text(title).font(.system(size: 20, weight: .bold, design: .rounded)); Spacer(); Button(action, action: onTap).font(.subheadline.weight(.semibold)).foregroundStyle(Palette.lime) }
}
