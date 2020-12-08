# read csv's
words <- read.csv("C:\\Users\\cml3t\\Desktop\\Shows\\word_counts_theoffice.csv", stringsAsFactors = FALSE);
ratings <- read.csv("C:\\Users\\cml3t\\Desktop\\Shows\\episode_ratings.csv", stringsAsFactors = FALSE);

# drop the duplicate columns
drop <- c("Show","Season","Episode")
words = words[,!(names(words) %in% drop)]

# join them
library("dplyr");
joined <- inner_join(words, ratings, by = "Episode_ID",stringsAsFactors = FALSE);

# drop the ones with bad data
# joined <- joined[joined$Drop=="No",];

# adjust the double episodes
# joined$Words <- ifelse(joined$Duplicate == "Yes", joined$Words / 2, joined$Words)

# get each shows frame 
himym <- joined[joined$Show=="How I Met Your Mother",];
office <- joined[joined$Show=="The Office",];
friends <- joined[joined$Show=="Friends",];

# HOW I MET YOUR MOTHER SECTION #
himym_results <- data.frame(Character = character(0), Coefficient = double(0), stringsAsFactors = FALSE);
characters <- unique(as.character(himym$Character));
for (char in characters) {
	# get correlation
	char <- as.character(char);
	trimmed <- himym[himym$Character == char,];
	correl = cor(trimmed$Words,trimmed$Rating);
	
	# get the new row to add
	newrow <- data.frame(Character = c(char), Coefficient = c(correl), stringsAsFactors = FALSE);
	himym_results <- rbind(newrow,himym_results);
}

# THE OFFICE SECTION #
office_results <- data.frame(Character = character(0), Coefficient = double(0), stringsAsFactors = FALSE);
characters <- unique(as.character(office$Character));
for (char in characters) {
	# get correlation
	char <- as.character(char);
	trimmed <- office[office$Character == char,];
	correl = cor(trimmed$Words,trimmed$Rating);
	
	# get the new row to add
	newrow <- data.frame(Character = c(char), Coefficient = c(correl), stringsAsFactors = FALSE);
	office_results <- rbind(newrow,office_results);
}

# FRIENDS SECTION #
friends_results <- data.frame(Character = character(0), Coefficient = double(0), stringsAsFactors = FALSE);
characters <- unique(as.character(friends$Character));
for (char in characters) {
	# get correlation
	char <- as.character(char);
	trimmed <- friends[friends$Character == char,];
	correl = cor(trimmed$Words,trimmed$Rating);
	
	# get the new row to add
	newrow <- data.frame(Character = c(char), Coefficient = c(correl), stringsAsFactors = FALSE);
	friends_results <- rbind(newrow,friends_results);
}

# save the office results to a csv
write.csv(office_results,"C:\\Users\\cml3t\\Desktop\\Shows\\office_results.csv", row.names = FALSE)