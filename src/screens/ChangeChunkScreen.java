package screens;

import model.SharedDownloadManager;
import screens.navigation.Navigation;
import screens.navigation.Route;

public class ChangeChunkScreen extends AbstractScreen {

    private final SharedDownloadManager  sharedDownloadManager;

    public ChangeChunkScreen(Navigation navigation, SharedDownloadManager sharedDownloadManager) {
        super(navigation);
        this.sharedDownloadManager = sharedDownloadManager;
    }

    @Override
    protected void showOptions() {
        System.out.println("Digite novo tamanho de chunk:");
    }

    @Override
    protected void onOptionSelected(int option) {
        this.sharedDownloadManager.setGlobalChunkSize(option);
        System.out.println("Tamanho de chunk alterado: " + option);
        navigation.navigate(Route.INITIAL);
    }

}
