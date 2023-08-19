package MainScript;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

import AutotalliVarasto.AutotalliJaVarastot;
import LomaAsunot.LomaAsuntoListHaku;
import Maajametsätilat.Maajametsätilat;
import Omistusasunot.Master;
import Omistusasunot.ScrapeList;
import Tontit.TontitListaHaku;

import static Util.Utility.Print;

public class Gui implements ActionListener {
	private JFrame frame = new JFrame();
	private JPanel panel = new JPanel();
	private JLabel Statustext;
	private JLabel Start;
	private JLabel Myydyt;
	private boolean Status = false;
	private int StatusInt;
	private Timer timer;
	private JTextField Http;
	public String ScrapreWebPage; // Get this URL from this script
	public Master Master;
	static ScrapeList ScrapeList = new ScrapeList();
	static LomaAsuntoListHaku LomaAsunto = new LomaAsuntoListHaku();
	static String onoff = "Off";
	static int MyytyAsuntoja;
	static JButton Hakunappi = new JButton("Haku");
	static JButton Lopetanappi = new JButton("Lopeta");
	static JCheckBox OmistusAsunot = new JCheckBox("Omistusasunot Scrape?");
	static public String OmistusAsunotKaikki = "https://www.etuovi.com/myytavat-asunnot?haku=M2005920847"; // Kaikki Omistus
	static String LomaAsunotKaikki = "https://www.etuovi.com/myytavat-loma-asunnot?haku=M2007539212"; // Kaikki
	static String Tontit = "https://www.etuovi.com/myytavat-tontit?haku=M2007540721";
	static String MaaJametsätilatKaikki = "https://www.etuovi.com/myytavat-maa-ja-metsatilat?haku=M2007545142";
	static String AutotallitVarastotKaikki = "https://www.etuovi.com/myytavat-autotallit-ja-muut?haku=M2007548754";

	static JCheckBox LomaAsunot = new JCheckBox("LomaAsunot Scrape?");
	static JCheckBox TontitCheckBox = new JCheckBox("Tontit Scrape?");
	static JCheckBox MaaJaMetsä = new JCheckBox("MaajaMetsä Scrape?");
	static JCheckBox AutotallitVarastot = new JCheckBox("AutotallitVarastot Scrape?");
	static String filePath = "C:\\Users\\pirue\\Documents\\GIthub\\Crawler\\Javawebscraper\\Addons\\MyydytKohteet.txt";

	
	public void Run(Master m) {
		Master = m; // Get url From gui and send it back(Line 106) to Master
	}
	public void RunLomaAsunotKaikki(Master m) {
		Master = m; // Get url From gui and send it back(Line 106) to Master
	}
	public void RunTontit(Master m) {
		Master = m; // Get url From gui and send it back(Line 106) to Master
	}
	public void RunMaaJametsätilatKaikki(Master m) {
		Master = m; // Get url From gui and send it back(Line 106) to Master
	}
	public void RunAutotallitVarastotKaikki(Master m) {
		Master = m; // Get url From gui and send it back(Line 106) to Master
	}
	
	
	public void updateMyytyAsuntoja() {
		MyytyAsuntoja++;
		Myydyt.setText("Myyty: " + MyytyAsuntoja);
	}

	public int MyytyAsuntoja() {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			int MyytyAsuntoja = 0;
			String line;

			while ((line = br.readLine()) != null) {
				MyytyAsuntoja++;
			}
			// Update the label with the new count
			Myydyt.setText("Myyty: " + MyytyAsuntoja);
			return MyytyAsuntoja; // Move the return statement inside the try block
		} catch (IOException e) {
			e.printStackTrace();
		}
		return MyytyAsuntoja; // Return 0 or some default value in case of an exception or other error
	}

	public Gui() {
//		panel.setBorder(BorderFactory.createEmptyBorder(100, 300, -100, 300));
//		panel.setLayout(new GridLayout(20, 1));

		// Initialize the Myydyt JLabel with an initial value before calling
		// MyytyAsuntoja method
		Myydyt = new JLabel("Myyty: 0");

		MyytyAsuntoja(); // Now call the MyytyAsuntoja method to get the actual count

		Start = new JLabel("Start: " + onoff);
		Statustext = new JLabel("Status " + "Pois päältä");

		Http = new JTextField(10);
		Hakunappi.addActionListener(this);
		Lopetanappi.addActionListener(this);

		panel.add(OmistusAsunot);
		panel.add(LomaAsunot);
		panel.add(TontitCheckBox);
		panel.add(MaaJaMetsä);
		panel.add(AutotallitVarastot);
		panel.add(Myydyt);
		panel.add(Start);
		panel.add(Statustext);
		// panel.add(Http); Left for if need use later..
		panel.add(Hakunappi);
		panel.add(Lopetanappi);
		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowFocusListener(null);
		frame.setTitle("Out Gui");
		frame.pack();
		frame.setVisible(true);

	}

	public String exportURL() {
		return Http.getText();
	}

	private void closeWindow() {
		StatusInt = 4;
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StatusInt--;
				Statustext.setText("Sammutetaan päästä " + String.valueOf(StatusInt) + ".");
				if (StatusInt == 0) {
					timer.stop();
					frame.dispose();
					System.exit(0);
				}
			}
		});
		timer.start();

	}

	@Override
	public void actionPerformed(ActionEvent i) {
		if (i.getActionCommand().equals("Lopeta")) {
			Statustext.setText("Sammutetaan");
			closeWindow();
			return; // Exit the actionPerformed method after stopping the script
		}

		if (i.getActionCommand().equals("Haku")) {
			onoff = "On";
			Start.setText("Start:  " + onoff);
			if (!Status) { // Start the script only if it's not already running
				// ScrapreWebPage = Http.getText(); // Store the URL to be scraped Left for if
				// need use later..
				try {
					if (OmistusAsunot.isSelected()) {
						ScrapeList.Run(OmistusAsunotKaikki);
					}
					if (TontitCheckBox.isSelected()) {
						TontitListaHaku.Run(Tontit, 260);
					}
					if (LomaAsunot.isSelected()) {
						LomaAsuntoListHaku.Run(LomaAsunotKaikki, 180);
					}
					if(MaaJaMetsä.isSelected()) {
						Maajametsätilat.Run(MaaJametsätilatKaikki, 35);
					}
					if(AutotallitVarastot.isSelected()) {
						AutotalliJaVarastot.Run(AutotallitVarastotKaikki, 150);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} // Start the scraping process
				Status = true; // Set the status to indicate the script is running
				Statustext.setText("WebScraper Päällä");
			}
		}
	}

}
