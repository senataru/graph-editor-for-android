declare module 'coreApp' {
	export interface CoreContext {
		registerGraphMenuOption(optionName : string, calledFunction : string): number;
		deregisterGraphMenuOption(id : number)
	}
}