import IMGLYEditor
import IMGLYEditorModule
import IMGLYEngine
import SwiftUI

/// A typealias for `EditorBuilder.EditorBuilderResult` for convenient usage.
typealias EditorBuilderResult = EditorBuilder.EditorBuilderResult

/// Manages the editor customizations.
@MainActor
@objc class Customizations: NSObject {
  /// Applies the customizations.
  @objc static func apply() {
    /// Returns a custom or default editor based on the metadata.
    /// - Parameters:
    ///   - defaultEditor: The default editor.
    ///   - customEditor: The custom editor.
    ///   - metadata: The metadata used to determine which editor to use.
    /// - Returns: The corresponding `EditorBuilder.Builder`.
    func defaultOrCustomEditor(
      defaultEditor: @escaping EditorBuilder.Builder,
      customEditor: @escaping EditorBuilder.Builder,
      metadata: [String: Any]?,
    ) -> EditorBuilder.Builder {
      if let enabled = metadata?["custom"] as? Bool, enabled == true {
        customEditor
      } else {
        defaultEditor
      }
    }

    IMGLYEditorModuleSwiftAdapter.shared.builderClosure = { preset, metadata in
      switch preset {
      case .postcard:
        defaultOrCustomEditor(
          defaultEditor: EditorBuilder.postcard(),
          customEditor: EditorBuilder.custom { settings, _, _, result in
            CustomPostcardEditor(settings: settings, result: result)
          },
          metadata: metadata,
        )
      case .photo:
        defaultOrCustomEditor(
          defaultEditor: EditorBuilder.photo(),
          customEditor: EditorBuilder.custom { settings, _, _, result in
            CustomPhotoEditor(settings: settings, result: result)
          },
          metadata: metadata,
        )
      case .video:
        defaultOrCustomEditor(
          defaultEditor: EditorBuilder.video(),
          customEditor: EditorBuilder.custom { settings, _, _, result in
            CustomVideoEditor(settings: settings, result: result)
          },
          metadata: metadata,
        )
      case .design:
        defaultOrCustomEditor(
          defaultEditor: EditorBuilder.design(),
          customEditor: EditorBuilder.custom { settings, _, _, result in
            CustomDesignEditor(settings: settings, result: result)
          },
          metadata: metadata,
        )
      case .apparel:
        defaultOrCustomEditor(
          // NOTE: This custom editor is only used for UITests.
          // By default, simply use `EditorBuilder.apparel()` instead.
          defaultEditor: EditorBuilder.custom { settings, _, _, result in
            ApparelEditorForUITests(settings: settings, result: result)
          },
          customEditor: EditorBuilder.custom { settings, _, _, result in
            CustomApparelEditor(settings: settings, result: result)
          },
          metadata: metadata,
        )
      case nil:
        EditorBuilder.custom { settings, _, _, result in
          CustomDesignEditor(settings: settings, result: result)
        }
      }
    }
  }
}

// MARK: - Custom Editors

/// An extension containing the custom editor implementations.
extension Customizations {
  /// Derives `EngineSettings` for a given `EditorSettings`.
  /// - Parameters:
  ///   - settings: The `EditorSettings` to derive the settings from.
  /// - Returns: The derived `EngineSettings`.
  private static func engineSettings(for settings: EditorSettings) -> EngineSettings {
    if let url = URL(string: settings.baseUri) {
      EngineSettings(license: settings.license, userID: settings.userId, baseURL: url)
    } else {
      EngineSettings(license: settings.license, userID: settings.userId)
    }
  }

  /// A custom design editor.
  private struct CustomDesignEditor: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            DesignEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await DesignEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await DesignEditorConfiguration.defaultOnCreate()(engine)
                }
                try engine.asset.addSource(UnsplashAssetSource(host: Secrets.unsplashHost))
              }
              builder.onExport { engine, _, _ in
                do {
                  let editorResult = try await OnExport.export(engine, .pdf)
                  result(.success(editorResult))
                } catch {
                  result(.failure(error))
                }
              }
              builder.assetLibrary { libBuilder in
                libBuilder.modify { categories in
                  categories.modifySections(of: AssetLibraryCategory.ID.images) { sections in
                    sections.addFirst(.image(
                      id: UnsplashAssetSource.id,
                      title: "Unsplash",
                      source: .init(id: UnsplashAssetSource.id),
                    ))
                  }
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }

  /// A custom apparel editor.
  private struct CustomApparelEditor: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            ApparelEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await ApparelEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await ApparelEditorConfiguration.defaultOnCreate()(engine)
                }
                try engine.asset.addSource(UnsplashAssetSource(host: Secrets.unsplashHost))

                // This is only needed for UITests.
                try engine.editor.setSettingBool("showBuildVersion", value: false)
              }
              builder.onExport { engine, _, _ in
                do {
                  let editorResult = try await OnExport.export(engine, .pdf)
                  result(.success(editorResult))
                } catch {
                  result(.failure(error))
                }
              }
              builder.assetLibrary { libBuilder in
                libBuilder.modify { categories in
                  categories.modifySections(of: AssetLibraryCategory.ID.images) { sections in
                    sections.addFirst(.image(
                      id: UnsplashAssetSource.id,
                      title: "Unsplash",
                      source: .init(id: UnsplashAssetSource.id),
                    ))
                  }
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }

  /// A custom photo editor.
  private struct CustomPhotoEditor: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            PhotoEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await PhotoEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await PhotoEditorConfiguration.defaultOnCreate()(engine)
                }
                try engine.asset.addSource(UnsplashAssetSource(host: Secrets.unsplashHost))
              }
              builder.onExport { engine, _, _ in
                do {
                  let editorResult = try await OnExport.export(engine, .png)
                  result(.success(editorResult))
                } catch {
                  result(.failure(error))
                }
              }
              builder.assetLibrary { libBuilder in
                libBuilder.modify { categories in
                  categories.modifySections(of: AssetLibraryCategory.ID.images) { sections in
                    sections.addFirst(.image(
                      id: UnsplashAssetSource.id,
                      title: "Unsplash",
                      source: .init(id: UnsplashAssetSource.id),
                    ))
                  }
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }

  /// A custom video editor.
  private struct CustomVideoEditor: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            VideoEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await VideoEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await VideoEditorConfiguration.defaultOnCreate()(engine)
                }
                try engine.asset.addSource(UnsplashAssetSource(host: Secrets.unsplashHost))
              }
              builder.onExport { engine, eventHandler, _ in
                do {
                  let editorResult = try await OnExport.exportVideo(engine, eventHandler, .mp4)
                  result(.success(editorResult))
                } catch {
                  if error is CancellationError { return }
                  result(.failure(error))
                }
              }
              builder.assetLibrary { libBuilder in
                libBuilder.modify { categories in
                  categories.modifySections(of: AssetLibraryCategory.ID.images) { sections in
                    sections.addFirst(.image(
                      id: UnsplashAssetSource.id,
                      title: "Unsplash",
                      source: .init(id: UnsplashAssetSource.id),
                    ))
                  }
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }

  /// A custom postcard editor.
  private struct CustomPostcardEditor: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            PostcardEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await PostcardEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await PostcardEditorConfiguration.defaultOnCreate()(engine)
                }
                try engine.asset.addSource(UnsplashAssetSource(host: Secrets.unsplashHost))
              }
              builder.onExport { engine, _, _ in
                do {
                  let editorResult = try await OnExport.export(engine, .pdf)
                  result(.success(editorResult))
                } catch {
                  result(.failure(error))
                }
              }
              builder.assetLibrary { libBuilder in
                libBuilder.modify { categories in
                  categories.modifySections(of: AssetLibraryCategory.ID.images) { sections in
                    sections.addFirst(.image(
                      id: UnsplashAssetSource.id,
                      title: "Unsplash",
                      source: .init(id: UnsplashAssetSource.id),
                    ))
                  }
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }

  /// An apparel editor for UI tests.
  private struct ApparelEditorForUITests: View {
    private let settings: EditorSettings
    private let result: EditorBuilderResult

    init(settings: EditorSettings, result: @escaping EditorBuilderResult) {
      self.settings = settings
      self.result = result
    }

    var body: some View {
      NavigationView {
        Editor(engineSettings(for: settings))
          .imgly.configuration {
            ApparelEditorConfiguration { builder in
              builder.onCreate { engine, _ in
                if let createScene = try OnCreate.loadFromSettings(settings) {
                  try await ApparelEditorConfiguration.defaultOnCreate(createScene: createScene)(engine)
                } else {
                  try await ApparelEditorConfiguration.defaultOnCreate()(engine)
                }

                // This is only needed for UITests.
                try engine.editor.setSettingBool("showBuildVersion", value: false)
              }
              builder.onExport { engine, _, _ in
                do {
                  let editorResult = try await OnExport.export(engine, .pdf)
                  result(.success(editorResult))
                } catch {
                  result(.failure(error))
                }
              }
            }
            ModalEditorConfiguration(result: result)
          }
      }.navigationViewStyle(.stack)
    }
  }
}

// MARK: - Unsplash

private final class UnsplashAssetSource: NSObject {
  private lazy var decoder: JSONDecoder = {
    let decoder = JSONDecoder()
    decoder.keyDecodingStrategy = .convertFromSnakeCase
    return decoder
  }()

  private let host: String
  private let path: String

  public init(host: String, path: String = "/unsplashProxy") {
    self.host = host
    self.path = path
  }

  private struct Endpoint {
    let path: String
    let query: [URLQueryItem]

    static func search(queryData: AssetQueryData) -> Self {
      Endpoint(
        path: "/search/photos",
        query: [
          .init(name: "query", value: queryData.query),
          .init(name: "page", value: String(queryData.page + 1)),
          .init(name: "per_page", value: String(queryData.perPage)),
          .init(name: "content_filter", value: "high"),
        ],
      )
    }

    static func list(queryData: AssetQueryData) -> Self {
      Endpoint(
        path: "/photos",
        query: [
          .init(name: "order_by", value: "popular"),
          .init(name: "page", value: String(queryData.page + 1)),
          .init(name: "per_page", value: String(queryData.perPage)),
          .init(name: "content_filter", value: "high"),
        ],
      )
    }

    func url(with host: String, path: String) -> URL? {
      var components = URLComponents()
      components.scheme = "https"
      components.host = host
      components.path = path + self.path
      components.queryItems = query
      return components.url
    }
  }
}

extension UnsplashAssetSource: AssetSource {
  static let id = "ly.img.asset.source.unsplash"

  var id: String {
    Self.id
  }

  func findAssets(queryData: AssetQueryData) async throws -> AssetQueryResult {
    let endpoint: Endpoint = queryData.query?
      .isEmpty ?? true ? .list(queryData: queryData) : .search(queryData: queryData)

    let data = try await URLSession.shared.get(endpoint.url(with: host, path: path)!).0

    if queryData.query?.isEmpty ?? true {
      let response = try decoder.decode(UnsplashListResponse.self, from: data)
      let nextPage = queryData.page + 1

      return .init(
        assets: response.map(AssetResult.init),
        currentPage: queryData.page,
        nextPage: nextPage,
        total: -1,
      )
    } else {
      let response = try decoder.decode(UnsplashSearchResponse.self, from: data)
      let (results, total, totalPages) = (response.results, response.total ?? 0, response.totalPages ?? 0)
      let nextPage = (queryData.page + 1) == totalPages ? -1 : queryData.page + 1

      return .init(
        assets: results.map(AssetResult.init),
        currentPage: queryData.page,
        nextPage: nextPage,
        total: total,
      )
    }
  }

  var supportedMIMETypes: [String]? {
    [MIMEType.jpeg.rawValue]
  }

  var credits: AssetCredits? {
    .init(
      name: "Unsplash",
      url: URL(string: "https://unsplash.com/")!,
    )
  }

  var license: AssetLicense? {
    .init(
      name: "Unsplash license (free)",
      url: URL(string: "https://unsplash.com/license")!,
    )
  }
}

private extension AssetResult {
  convenience init(image: UnsplashImage) {
    self.init(
      id: image.id,
      locale: "en",
      label: image.description ?? image.altDescription,
      tags: image.tags?.compactMap(\.title),
      meta: [
        "uri": image.urls.full.absoluteString,
        "thumbUri": image.urls.thumb.absoluteString,
        "blockType": DesignBlockType.graphic.rawValue,
        "fillType": FillType.image.rawValue,
        "shapeType": ShapeType.rect.rawValue,
        "kind": "image",
        "width": String(image.width),
        "height": String(image.height),
      ],
      context: .init(sourceID: "unsplash"),
      credits: .init(name: image.user.name!, url: image.user.links?.html),
      utm: .init(source: "CE.SDK Demo", medium: "referral"),
    )
  }
}

private extension URLSession {
  // https://forums.developer.apple.com/forums/thread/727823
  // Silences warning: "Non-sendable type '(any URLSessionTaskDelegate)?' exiting main actor-isolated context in call to
  // non-isolated instance method 'data(from:delegate:)' cannot cross actor boundary"
  nonisolated func get(_ url: URL) async throws -> (Data, URLResponse) {
    try await data(from: url)
  }
}

// MARK: - UnsplashResponse

private struct UnsplashSearchResponse: Decodable {
  let total, totalPages: Int?
  let results: [UnsplashImage]
}

private typealias UnsplashListResponse = [UnsplashImage]

// MARK: - Result

private struct UnsplashImage: Decodable {
  let id: String
  let createdAt, updatedAt: String
  let promotedAt: String?
  let width, height: Int
  let color, blurHash: String?
  let description: String?
  let altDescription: String?
  let urls: Urls
  let likes: Int?
  let likedByUser: Bool?
  let user: User
  let tags: [Tag]?
}

// MARK: - Tag

private struct Tag: Decodable {
  let type, title: String?
}

// MARK: - Urls

private struct Urls: Decodable {
  let raw, full, regular, small: URL
  let thumb, smallS3: URL
}

// MARK: - User

private struct User: Decodable {
  let id: String
  let updatedAt: String

  let username, name, firstName: String?
  let lastName, twitterUsername: String?
  let portfolioURL: String?
  let bio, location: String?
  let links: UserLinks?
  let instagramUsername: String?
  let totalCollections, totalLikes, totalPhotos: Int?
  let acceptedTos, forHire: Bool?
}

// MARK: - UserLinks

private struct UserLinks: Decodable {
  let linksSelf, html, photos, likes: URL?
  let portfolio, following, followers: URL?
}
