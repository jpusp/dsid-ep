package screens;

import model.DownloadStats;
import model.SharedDownloadManager;
import screens.navigation.Navigation;
import screens.navigation.Route;

public class DisplayStatisticsScreen extends AbstractScreen {

    private final SharedDownloadManager  sharedDownloadManager;

    public DisplayStatisticsScreen(Navigation navigation, SharedDownloadManager sharedDownloadManager) {
        super(navigation);
        this.sharedDownloadManager = sharedDownloadManager;
    }

    @Override
    protected void showOptions() {
        System.out.printf("%-12s | %-10s | %-20s | %-4s | %-15s | %-15s\n",
                "Tam. chunk","N Peers", "Tamanho Arquivo", "N", "Tempo Médio", "Desvio Padrão");
        for(DownloadStats stat: sharedDownloadManager.getStats()) {
            System.out.printf("%-12d | %-10d | %-20d | %-4d | %-15d | %-15d\n",
                    stat.getChunkSize(),
                    stat.getPeersSize(),
                    stat.getFileSize(),
                    stat.getSampleSize(),
                    stat.getAverageMileseconds(),
                    stat.getDesvpad()
            );
        }
        navigation.navigate(Route.INITIAL);
    }

    @Override
    protected void onOptionSelected(int option) {
        navigation.navigate(Route.INITIAL);
    }

}
