> [!WARNING]
>
> - This is a release candidate [(see definition)](https://en.wikipedia.org/wiki/Software_release_life_cycle#Release_candidate), used for final public testing and translation. *It is still a preliminary release*
> - You should test all releases, with actual data, *several days* before a competition. This is especially important when considering the use of a release candidate.

| Introducing the Owlcms Control Panel                         |
| ------------------------------------------------------------ |
| Starting with release 55, the installation process for running owlcms on a local machine changes.<br><br>Previously, only Windows had a full installer.  From now on all platforms will use the same installation process, using a new Control Panel program that is available for Windows, macOS, RaspberryPi OS and Linux.<br><br>After installing the Control Panel on your computer (see below for instructions), it is used<ul><li>to install and update owlcms and<li> to start and stop the program. </ul>See the [Control Panel Installation Instructions](https://owlcms.github.io/owlcms4-prerelease/#/LocalDownloads.md) and the instructions for using the [owlcms Control Panel](https://owlcms.github.io/owlcms4-prerelease/#/LocalControlPanel.md).<br><br>As a result of this change, this repository will only contain the Java output that is used on all platforms. |

Version Log

- 55.0.0-rc01: Mac installer for Intel mac has been renamed, adjusted documentation
- 55.0.0-beta02: Minor fixes to the documentation
- 55.0.0-beta01: Added per-session explicit override of clean & jerk break duration

**New In This Release**

- New [Installation Instructions](https://owlcms.github.io/owlcms4-prerelease/#/LocalDownloads.) and startup instructions using the [owlcms Control Panel](https://owlcms.github.io/owlcms4-prerelease/#/LocalControlPanel.md) for updating, launching and stopping OWLCMS on a local computer.

- Ability to set the duration of the clean & jerk break explicitly for a session, overriding the competition-wide rules.

  - A new Excel template variable `${session.cleanJerkBreakMinutes}` can be used to show this to the announcer if you have a specific template for athlete introductions

- Simplified Video Setup
  - The default style for Video Streaming is now `transparent` 
    With this change,
    
    - It is no longer necessary to crop the Current Athlete view
    - There is no need to add a green mask to have a floating scoreboard
    
    See the documentation on using [OBS](https://owlcms.github.io/owlcms4-prerelease/#/LocalDownloads.) for examples of using the transparent style.
    
  - The style can be changed back to `nogrid` on the System Settings > Customization page to get the black background styles identical to the on-site scoreboards.

    


For other recent changes, see [version 54 release notes](https://github.com/owlcms/owlcms4/releases/tag/54.2.1) and [version 53 release notes](https://github.com/owlcms/owlcms4/releases/tag/53.1.0)
