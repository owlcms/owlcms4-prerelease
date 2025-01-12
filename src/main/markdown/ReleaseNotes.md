> [!CAUTION]
>
> - This is an **alpha release**, used for validating new features.  *Some features are likely to be incomplete or non-functional*.
> - **Alpha releases are not normally used in actual competitions.** 
> - Export your current database before updating if it contains important data.

| Introducing the Owlcms Control Panel                         |
| ------------------------------------------------------------ |
| Starting with release 55, the installation process changes for running owlcms on a local machine.<br />A small program is now installed (once) on your computer. It provides a Control Panel to install and update owlcms and to start and stop the program  The control panel works the same on Windows, macOS, RasperryPi OS and Linux.<br />See the [Installation Instructions](https://${env.REPO_OWNER}.github.io/${env.O_REPO_NAME}/#/LocalDownloads.) and execution instructions using the [owlcms Control Panel](https://${env.REPO_OWNER}.github.io/${env.O_REPO_NAME}/#/LocalControlPanel.md). |

Version Log

- 55.0.0-alpha04: Updated the Mac instructions for correct DMG drag description
- 55.0.0-alpha04: Removed obsolete Procfile and system.properties files from packaging
- 55.0.0-alpha03: Updated the Mac and Windows instructions to use the Control Panel

**New In This Release**

- New [Installation Instructions](https://${env.REPO_OWNER}.github.io/${env.O_REPO_NAME}/#/LocalDownloads.) and startup instructions using the [owlcms Control Panel](https://${env.REPO_OWNER}.github.io/${env.O_REPO_NAME}/#/LocalControlPanel.md) for updating, launching and stopping OWLCMS on a local computer.
- Simplified Video Setup
  - The default style for Video Streaming is now `transparent` . When using `transparent` 
    - It is no longer necessary to crop the Current Athlete view
    - There is no need to add a green mask to have a floating scoreboard

  - The style can be changed back to `nogrid` on the System Settings > Customization page to get the black background styles identical to the on-site scoreboards.


For other recent changes, see [version 54 release notes](https://github.com/owlcms/owlcms4/releases/tag/54.2.1) and [version 53 release notes](https://github.com/owlcms/owlcms4/releases/tag/53.1.0)
