Pod::Spec.new do |s|
  s.name             = 'KmpDracoNative'
  s.version          = '0.2.0'
  s.summary          = 'iOS native Draco bitstream decoder (xcframework)'
  s.description      = 'Vendored libkmp_draco_native for iOS. Use with Maven io.github.sofarnobug:kmp-draco for the Kotlin API.'
  s.homepage         = 'https://github.com/SoFarNoBug/kmp-draco'
  s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
  s.author           = { 'jlj' => '1396275290@qq.com' }
  s.platform         = :ios, '15.0'
  s.source           = { :git => 'https://github.com/SoFarNoBug/kmp-draco.git', :tag => s.version.to_s }
  s.vendored_frameworks = 'kmp-draco-native/dist/ios/KmpDracoNative.xcframework'
  s.requires_arc     = true
  s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
  s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
end
