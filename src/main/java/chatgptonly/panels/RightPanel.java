package chatgptonly.panels;

import static chatgptonly.constants.WorldConstants.SCREEN_H;
import static chatgptonly.constants.WorldConstants.SCREEN_W;

public class RightPanel {

    public MenuPanel menuPanel;
    public InfoPanel infoPanel;

    public RightPanel() {
        menuPanel = new MenuPanel();
        infoPanel = new InfoPanel();
    }
}
