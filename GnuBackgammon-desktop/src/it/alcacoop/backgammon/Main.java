/*
 ##################################################################
 #                     GNU BACKGAMMON MOBILE                      #
 ##################################################################
 #                                                                #
 #  Authors: Domenico Martella - Davide Saurino                   #
 #  E-mail: info@alcacoop.it                                      #
 #  Date:   19/12/2012                                            #
 #                                                                #
 ##################################################################
 #                                                                #
 #  Copyright (C) 2012   Alca Societa' Cooperativa                #
 #                                                                #
 #  This file is part of GNU BACKGAMMON MOBILE.                   #
 #  GNU BACKGAMMON MOBILE is free software: you can redistribute  # 
 #  it and/or modify it under the terms of the GNU General        #
 #  Public License as published by the Free Software Foundation,  #
 #  either version 3 of the License, or (at your option)          #
 #  any later version.                                            #
 #                                                                #
 #  GNU BACKGAMMON MOBILE is distributed in the hope that it      #
 #  will be useful, but WITHOUT ANY WARRANTY; without even the    #
 #  implied warranty of MERCHANTABILITY or FITNESS FOR A          #
 #  PARTICULAR PURPOSE.  See the GNU General Public License       #
 #  for more details.                                             #
 #                                                                #
 #  You should have received a copy of the GNU General            #
 #  Public License v3 along with this program.                    #
 #  If not, see <http://http://www.gnu.org/licenses/>             #
 #                                                                #
 ##################################################################
*/

package it.alcacoop.backgammon;

import it.alcacoop.backgammon.gservice.GServiceClient;
import it.alcacoop.backgammon.logic.AICalls;
import it.alcacoop.backgammon.ui.UIDialog;
import it.alcacoop.backgammon.utils.MatchRecorder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import bsh.Interpreter;
import bsh.util.JConsole;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.SharedLibraryLoader;


public class Main implements NativeFunctions {
  private static Main instance;
  private static String data_dir;
  private static Interpreter bsh;
  private static JConsole mScriptConsole;
  private static BeanShellEditor mScriptEditor;


  public static void main(String[] args) {
    LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "GnuBackgammon";
    cfg.width = 800;
    cfg.height = 480;
    instance = new Main();
    new LwjglApplication(new GnuBackgammon(instance), cfg);

    new SharedLibraryLoader("libs/gnubg.jar").load("gnubg");
    String s = System.getProperty("user.dir");
    data_dir = s;
    s += "/libs/";
    AICalls.InitializeEnvironment(s);

    mScriptConsole = new JConsole();
    bsh = new Interpreter(mScriptConsole);
    mScriptEditor = new BeanShellEditor();
    new Thread(bsh).start();
    setBsh("devconsole", mScriptConsole);
    setBsh("deveditor", mScriptEditor);
    setBsh("bsh", bsh);
    runBsh(Gdx.files.internal("libs/devtools.bsh").path());
  }

  @Override
  public void showAds(boolean show) {}

  protected Object handler = new Object() {};


  @Override
  public String getDataDir() {
    return data_dir;
  }

  @Override
  public void shareMatch(MatchRecorder rec) {
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmm");
    Date date = new Date();
    String d = dateFormat.format(date);

    String path = Gdx.files.external("data/gnubg-sgf/match-" + d + ".sgf").path();

    FileHandle fh = Gdx.files.absolute(path);
    Writer writer = fh.writer(false);
    try {
      writer.write(rec.saveSGF());
      writer.flush();
      writer.close();
    } catch (IOException e) {}
  }


  /* NEW CODE */
  public static void evalBsh(String cmds) {
    try {
      bsh.eval(cmds);
    } catch (bsh.EvalError e) {}
  }

  public static void runBsh(String filename) {
    String bsh_text = "";
    try {
      bsh_text = getContent(filename);
    } catch (IOException e1) {}


    try {
      bsh.eval(bsh_text);
    } catch (bsh.EvalError e) {}
  }

  public static void setBsh(String where, Object what) {
    try {
      bsh.set(where, what);
    } catch (bsh.EvalError e) {}
  }


  public static String getContent(String fname) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(fname));
    String text = "";
    try {
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
      text = sb.toString();

    } catch (Exception e) {} finally {
      br.close();
    }
    return text;
  }

  @Override
  public void injectBGInstance() {
    setBsh("disp", GnuBackgammon.Instance.commandDispatcher);
    setBsh("fibs", GnuBackgammon.Instance.fibs);
    setBsh("gbg", GnuBackgammon.Instance);
    setBsh("gservice", GServiceClient.getInstance());
  }

  @Override
  public void fibsSignin() {
    UIDialog.getLoginDialog();
  }

  @Override
  public void fibsRegistration() {}

  @Override
  public boolean isNetworkUp() {
    return true;
  }

  @Override
  public void hideChatBox() {}

  @Override
  public void showInterstitial() {}

  @Override
  public void initEngine() {}

  @Override
  public void showChatBox() {}

  @Override
  public boolean isProVersion() {
    return false;
  }

  @Override
  public void inAppBilling() {}

  @Override
  public void gserviceSignIn() {}

  @Override
  public boolean gserviceIsSignedIn() {
    return false;
  }

  @Override
  public void gserviceStartRoom() {}

  @Override
  public void gserviceAcceptInvitation(String invitationId) {}

  @Override
  public void gserviceSendReliableRealTimeMessage(String msg) {}

  @Override
  public void gserviceResetRoom() {}

  @Override
  public void gserviceOpenLeaderboards() {}

  @Override
  public void gserviceOpenAchievements() {}

  @Override
  public void hideProgressDialog() {}

  @Override
  public void gserviceUpdateAchievement(String achievement_id, int increment) {}

  @Override
  public void gserviceUnlockAchievement(String achiev_id) {}

  @Override
  public void gserviceUpdateState() {}

  @Override
  public void gserviceGetSigninDialog(int from) {}

  @Override
  public void gserviceSubmitRating(long score, String board_id) {}

  @Override
  public int getAppVersionCode() {
    return 0;
  }

  @Override
  public String gservicePendingNotificationAreaInvitation() {
    return null;
  }

  @Override
  public void openURL(String... url) {}

  @Override
  public void beginGoogleSignIn() {}

  @Override
  public void loadImageFromIconURI(Object iconURI, int playerIndex) {}
}
