import SwiftUI

@main
struct Cam2CookApp: App {
    @StateObject private var store = InventoryStore()
    var body: some Scene {
        WindowGroup {
            MainTabView()
                .environmentObject(store)
                .preferredColorScheme(.dark)
        }
    }
}

struct FoodItem: Identifiable, Codable, Equatable {
    var id = UUID()
    var name: String
    var category: String
    var quantity: String
    var location: String
    var daysLeft: Int
    var symbol: String
    var tint: String
    var consumed = false

    var status: String {
        if daysLeft < 0 { return "Review" }
        if daysLeft == 0 { return "Today" }
        if daysLeft <= 2 { return "Use soon" }
        return "Fresh"
    }
}

@MainActor final class InventoryStore: ObservableObject {
    @Published var items: [FoodItem] = [
        .init(name: "Avocado", category: "Produce", quantity: "2 pieces", location: "Fridge", daysLeft: 1, symbol: "🥑", tint: "green"),
        .init(name: "Cherry tomatoes", category: "Produce", quantity: "350 g", location: "Fridge", daysLeft: 2, symbol: "🍅", tint: "red"),
        .init(name: "Whole milk", category: "Dairy", quantity: "1 bottle", location: "Fridge", daysLeft: 0, symbol: "🥛", tint: "blue"),
        .init(name: "Spinach", category: "Produce", quantity: "1 bunch", location: "Fridge", daysLeft: 3, symbol: "🥬", tint: "green"),
        .init(name: "Eggs", category: "Dairy", quantity: "6 pieces", location: "Fridge", daysLeft: 8, symbol: "🥚", tint: "orange"),
        .init(name: "Sourdough bread", category: "Bakery", quantity: "1 loaf", location: "Pantry", daysLeft: 4, symbol: "🍞", tint: "orange")
    ]
    func add(_ item: FoodItem) { items.insert(item, at: 0) }
    func consume(_ item: FoodItem) { items.removeAll { $0.id == item.id } }
}
