#!/bin/bash

# Define the copyright notice
copyright_notice='/*******************************************************************************
 * Copyright © 2009-present Jean-François Lamy
 *
 * Licensed under the Non-Profit Open Software License version 3.0  ("NPOSL-3.0")
 * License text at https://opensource.org/licenses/NPOSL-3.0
 *******************************************************************************/'

# List of files to update (add your file paths here)
files=(
./owlcms/src/main/java/app/owlcms/AppShell.java
./owlcms/src/main/java/app/owlcms/apputils/JpaJsonConverter.java
./owlcms/src/main/java/app/owlcms/apputils/LogbackConfigReloader.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/BaseContent.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/DisplayParametersReader.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/ParameterReader.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/ResultsParameters.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/ResultsParametersReader.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/SoundParameters.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/SoundParametersReader.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/TopParameters.java
./owlcms/src/main/java/app/owlcms/apputils/queryparameters/TopParametersReader.java
./owlcms/src/main/java/app/owlcms/components/GroupCategorySelectionMenu.java
./owlcms/src/main/java/app/owlcms/components/GroupSelectionMenu.java
./owlcms/src/main/java/app/owlcms/data/agegroup/AssignedAthletesException.java
./owlcms/src/main/java/app/owlcms/data/agegroup/ChampionshipType.java
./owlcms/src/main/java/app/owlcms/data/athleteSort/Ranking.java
./owlcms/src/main/java/app/owlcms/data/export/AthleteSessionDataReader.java
./owlcms/src/main/java/app/owlcms/data/group/AgeGroupInfo.java
./owlcms/src/main/java/app/owlcms/data/group/AgeGroupInfoFactory.java
./owlcms/src/main/java/app/owlcms/data/group/BWCatInfo.java
./owlcms/src/main/java/app/owlcms/data/group/DisplayGroup.java
./owlcms/src/main/java/app/owlcms/data/jpa/HikariDataSourcePoolDetail.java
./owlcms/src/main/java/app/owlcms/data/records/RecordConfig.java
./owlcms/src/main/java/app/owlcms/data/records/RecordFilter.java
./owlcms/src/main/java/app/owlcms/displays/scoreboard/ResultsJury.java
./owlcms/src/main/java/app/owlcms/displays/scoreboard/ResultsLiftingOrder.java
./owlcms/src/main/java/app/owlcms/displays/scoreboard/ResultsRankingOrder.java
./owlcms/src/main/java/app/owlcms/displays/scoreboard/ResultsRankings.java
./owlcms/src/main/java/app/owlcms/displays/top/AbstractTop.java
./owlcms/src/main/java/app/owlcms/displays/video/StylesDirSelection.java
./owlcms/src/main/java/app/owlcms/fieldofplay/CountdownType.java
./owlcms/src/main/java/app/owlcms/init/MoquetteAuthenticator.java
./owlcms/src/main/java/app/owlcms/jetty/EmbeddedJetty.java
./owlcms/src/main/java/app/owlcms/monitors/IUnregister.java
./owlcms/src/main/java/app/owlcms/monitors/MQTTMonitor.java
./owlcms/src/main/java/app/owlcms/nui/displays/AbstractDisplayPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/attemptboards/AbstractAttemptBoardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/attemptboards/AthleteFacingAttemptBoardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/attemptboards/AthleteFacingDecisionBoardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/attemptboards/PublicFacingAttemptBoardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/attemptboards/PublicFacingDecisionBoardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/AbstractResultsDisplayPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/CurrentAthletePage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/JuryScoreboardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/MedalsPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/PublicMultiRanksPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/PublicNoLeadersPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/PublicRankingOrderPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/PublicScoreboardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/RankingsPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/WarmupLiftingOrderPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/WarmupMultiRanksPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/WarmupNoLeadersPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/WarmupRankingOrderPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/scoreboards/WarmupScoreboardPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/SoundEntries.java
./owlcms/src/main/java/app/owlcms/nui/displays/top/TopSinclairPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/top/TopTeamsPage.java
./owlcms/src/main/java/app/owlcms/nui/displays/top/TopTeamsSinclairPage.java
./owlcms/src/main/java/app/owlcms/nui/home/DisplayLoginView.java
./owlcms/src/main/java/app/owlcms/nui/lifting/NextAthleteAble.java
./owlcms/src/main/java/app/owlcms/nui/preparation/DocumentsGrid.java
./owlcms/src/main/java/app/owlcms/nui/preparation/PreCompetitionTemplates.java
./owlcms/src/main/java/app/owlcms/nui/preparation/RecordConfigEditingFormFactory.java
./owlcms/src/main/java/app/owlcms/nui/preparation/SessionGrid.java
./owlcms/src/main/java/app/owlcms/nui/results/IFilterCascade.java
./owlcms/src/main/java/app/owlcms/nui/results/SessionSelectionGrid.java
./owlcms/src/main/java/app/owlcms/nui/shared/HasBoardMode.java
./owlcms/src/main/java/app/owlcms/nui/shared/OwlcmsLayout.java
./owlcms/src/main/java/app/owlcms/spreadsheet/IRegistrationFileProcessor.java
./owlcms/src/main/java/app/owlcms/spreadsheet/MAthlete.java
./owlcms/src/main/java/app/owlcms/spreadsheet/NRegistrationFileProcessor.java
./owlcms/src/main/java/app/owlcms/spreadsheet/ORegistrationFileProcessor.java
./owlcms/src/main/java/app/owlcms/spreadsheet/XLSXAgeGroupsExport.java
./owlcms/src/main/java/app/owlcms/uievents/AppEvent.java
./owlcms/src/test/java/app/owlcms/data/jpa/BenchmarkDataTest.java
./owlcms/src/test/java/app/owlcms/tests/AgeFactorsTest.java
./owlcms/src/test/java/app/owlcms/tests/GAMXTest.java
./owlcms/src/test/java/app/owlcms/tests/JSONExportImportTest.java
./publicresults/src/main/java/app/owlcms/apputils/queryparameters/ContentParameters.java
./publicresults/src/main/java/app/owlcms/displays/scoreboard/HasBoardMode.java
./publicresults/src/main/java/app/owlcms/prutils/CountdownTimer.java
./publicresults/src/main/java/app/owlcms/prutils/SessionCleanup.java
./publicresults/src/main/java/app/owlcms/publicresults/AppShell.java
./publicresults/src/main/java/app/owlcms/publicresults/Traceable.java
./shared/src/main/java/app/owlcms/servlet/EmbeddedJetty.java
./shared/src/main/java/app/owlcms/servlet/StopProcessingException.java
./shared/src/main/java/app/owlcms/utils/CSSUtils.java
./shared/src/main/java/app/owlcms/utils/DelayTimer.java
./shared/src/main/java/app/owlcms/utils/OwlcmsLicense.java
./shared/src/main/java/app/owlcms/utils/ProcessUtils.java
)

# Add the copyright notice to each file
for file in "${files[@]}"; do
  if [ -f "$file" ]; then
    # Add the copyright notice at the top of the file
    echo "$copyright_notice" | cat - "$file" > temp && mv temp "$file"
    echo "Updated: $file"
  else
    echo "File not found: $file"
  fi
done
