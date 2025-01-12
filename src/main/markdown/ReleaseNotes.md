> [!CAUTION]
>
> - This is an **alpha release**, used for validating new features.  *Some features are likely to be incomplete or non-functional*.
> - **Alpha releases are not normally used in actual competitions.** 
> - Export your current database before updating if it contains important data.

| Introducing the Owlcms Control Panel                         |
| ------------------------------------------------------------ |
| Starting with release 55, the installation process for running owlcms on a local machine changes.<br><br>Previously, only Windows had a full installer.  From now on all platforms will use the same installation process, using a new Control Panel program that is available for Windows, macOS, RaspberryPi OS and Linux.<br><br>After installing the Control Panel on your computer (see below for instructions), it is used<ul><li>to install and update owlcms and<li> to start and stop the program. </ul>See the [Control Panel Installation Instructions](https://owlcms.github.io/owlcms4-prerelease/#/LocalDownloads.md) and the instructions for using the [owlcms Control Panel](https://owlcms.github.io/owlcms4-prerelease/#/LocalControlPanel.md).<br><br>As a result of this change, this repository will only contain the Java output that is used on all platforms. |

Version Log

- 55.0.0-alpha05: Tweaks to the control panel installation instructions
- 55.0.0-alpha04: Updated the Mac instructions for correct DMG drag description
- 55.0.0-alpha04: Removed obsolete Procfile and system.properties files from packaging
- 55.0.0-alpha03: Updated the Mac and Windows instructions to use the Control Panel

**New In This Release**

- New [Installation Instructions](https://owlcms.github.io/owlcms4-prerelease/#/LocalDownloads.) and startup instructions using the [owlcms Control Panel](https://owlcms.github.io/owlcms4-prerelease/#/LocalControlPanel.md) for updating, launching and stopping OWLCMS on a local computer.
- Simplified Video Setup
  - The default style for Video Streaming is now `transparent` . When using `transparent` 
    - It is no longer necessary to crop the Current Athlete view
    - There is no need to add a green mask to have a floating scoreboard

  - The style can be changed back to `nogrid` on the System Settings > Customization page to get the black background styles identical to the on-site scoreboards.


For other recent changes, see [version 54 release notes](https://github.com/owlcms/owlcms4/releases/tag/54.2.1) and [version 53 release notes](https://github.com/owlcms/owlcms4/releases/tag/53.1.0)
