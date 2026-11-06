const { getDefaultConfig } = require('expo/metro-config');

const config = getDefaultConfig(__dirname);
config.resolver.assetExts.push('scene', 'imgly'); // Add your custom extension here
module.exports = config;
