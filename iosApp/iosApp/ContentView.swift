import UIKit
import SwiftUI
import PiPixivKit

struct ComposeView: UIViewControllerRepresentable {
    @Binding var isStatusBarHidden: Bool

    func makeUIViewController(context: Context) -> UIViewController {
        PiPixiv.shared.makeMainViewController { hidden in
            DispatchQueue.main.async {
                withAnimation(.easeInOut(duration: 0.2)) {
                    isStatusBarHidden = hidden
                }
            }
        }
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    @State private var isStatusBarHidden = false

    var body: some View {
        ComposeView(isStatusBarHidden: $isStatusBarHidden)
            .ignoresSafeArea()
            .modifier(StatusBarVisibility(isHidden: isStatusBarHidden))
    }
}

private struct StatusBarVisibility: ViewModifier {
    let isHidden: Bool

    func body(content: Content) -> some View {
        // ToolbarPlacement.statusBar requires SwiftUI 8 from the iOS 27 SDK.
        #if canImport(SwiftUI, _version: 8.0)
        if #available(iOS 27.0, *) {
            content.toolbarVisibility(isHidden ? .hidden : .visible, for: .statusBar)
        } else {
            content.statusBarHidden(isHidden)
        }
        #else
        content.statusBarHidden(isHidden)
        #endif
    }
}
