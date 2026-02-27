import { NativeModules } from 'react-native';

const { RNFloatingBubble } = NativeModules;

if (!RNFloatingBubble) {
    console.warn('[RNFloatingBubble] Native module is null. Ensure you have rebuilt the native app.');
}

export const reopenApp = () => RNFloatingBubble ? RNFloatingBubble.reopenApp() : Promise.resolve();
export const showFloatingBubble = (x = 50, y = 100, iconPath = "") => RNFloatingBubble ? RNFloatingBubble.showFloatingBubble(x, y, iconPath) : Promise.resolve();
export const hideFloatingBubble = () => RNFloatingBubble ? RNFloatingBubble.hideFloatingBubble() : Promise.resolve();
export const checkPermission = () => RNFloatingBubble ? RNFloatingBubble.checkPermission() : Promise.resolve(false);
export const requestPermission = () => RNFloatingBubble ? RNFloatingBubble.requestPermission() : Promise.resolve();
export const initialize = () => RNFloatingBubble ? RNFloatingBubble.initialize() : Promise.resolve();

export default { showFloatingBubble, hideFloatingBubble, requestPermission, checkPermission, initialize, reopenApp };
