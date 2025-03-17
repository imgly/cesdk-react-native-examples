import Foundation

class Secrets {
  static var unsplashHost: String = ProcessInfo.processInfo.environment["CESDK_UNSPLASH_HOST"] ?? ""
}
