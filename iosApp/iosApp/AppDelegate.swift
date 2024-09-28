//
//  AppDelegate.swift
//  iosApp
//
//  Created by Daniel Alves on 28/09/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import UIKit
import SwiftUI
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate{
    var window: UIWindow?
    
    func application(_ application: UIApplication,
                     didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        KoinKtKt.doInitKoinIOS()
        let contentView = ContentView()
        let window = UIWindow(frame: UIScreen.main.bounds)
        window.rootViewController = UIHostingController(rootView: contentView)
        self.window = window
        window.makeKeyAndVisible()
        return true
    }
}

func application(_ application: UIApplication,
                    configurationForConnecting connectingSceneSession: UISceneSession,
                    options: UIScene.ConnectionOptions) -> UISceneConfiguration {
       return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
   }

