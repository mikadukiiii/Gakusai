Semantic extension of the Physical Web™ project
============

This is the original version of my final project for the Web Languages and Technologies course (Master’s Degree in Computer Engineering, [Technical University of Bari][poliba]). In March 2016, the project has been selected for the [Google Internet of Things (IoT) Technology Award][GoogleIoT], presented by the [Information Systems Research Lab][sisinflab], Technical University of Bari. Google will provide IoT technology prototypes and support to develop the project.

Check out the project website (built by SisInfLab fellows): <http://sisinflab.poliba.it/swottools/physicalweb>

What is it?
------------
It's a semantic web-based extension for the [Physical Web™][physicalweb] (project open-sourced since October 2014). It takes advantage of the [MiniME reasoning engine][minime] in order to compute similarity scores between semantic descriptions exposed by [Eddystone™][eddystone] beacons and a semantic annotation that describes some user preferences. A complete framework description is available in [docs/RelazioneLTW.pdf](docs/RelazioneLTW.pdf) (Italian), which is my original final report for the WLT exam. I left it as it is for sake of integrity, I may write an English version but keep in mind that this is the original report, presented in April 2015. In [docs/ulterioriSviluppi.pdf](docs/ulterioriSviluppi.pdf) you find some other ideas I shared with [Saverio Ieva][ieva] during the project development.

**_Please also remember that the project hasn't been maintained since April 2015_**

How does it work?
------------
The Android client is able to scan [Eddystone™-URL][eddystone] beacons and gathering optionally associated semantic information. Given an OWL ontology and a semantic-based user preferences annotation, these resources are ranked through logic-based approach and algorithms, in order to have on top of the list the most interesting items for the user. Other experimental features like history, bookmarks and spam marker are also included. For more information, please refer to [docs/RelazioneLTW.pdf](docs/RelazioneLTW.pdf)

The ontology and semantic annotations involved in the real case scenarios described in the final report are all included in the [scenarios/](scenarios/) folder.

Credits
------------
Most of the ideas behind this project have been elaborated from [Saverio Ieva][ieva]. He gave me great support and feedback on the whole idea and he is the only other active contributor to this project.

[Physical Web™][physicalweb] an open web discovery layer built on top of [Eddystone™][eddystone], is an open-source project. You can check it out at this address: <https://google.github.io/physical-web/>

The [MiniME reasoning engine][minime] is a software developed by [SisInfLab][sisinflab] and is distributed for academic purposes only. Some parts of the [annotationmanager](annotationmanager/) module and the entire package [it.poliba.sisinflab.ontologybrowser](app/src/main/java/it/poliba/sisinflab/ontologybrowser) have been developed by [SisInfLab][sisinflab] fellows over the years for other projects, and I just reused their code. For more information about their research activity, please refer to: <http://sisinflab.poliba.it/swottools>

Contributors
------------

* [Saverio Ieva][ieva]
* [Giorgio Basile][basile] (me :D)
* [Floriano Scioscia][scioscia]
* [Filippo Gramegna][gramegna]

Publications
------------

[1] M. Ruta, S. Ieva, G. Loseto, E. Di Sciascio, [From the Physical Web to the Physical Semantic Web: Knowledge Discovery in the Internet of Things](http://sisinflab.poliba.it/publications/2016/RILD16/), The Tenth International Conference on Mobile Ubiquitous Computing, Systems, Services and Technologies (UBICOMM 2016) - oct 2016

[2] M. Ruta, F. Scioscia, S. Ieva, G. Loseto, F. Gramegna, A. Pinto, E. Di Sciascio, [Knowledge discovery and sharing in the IoT: the Physical Semantic Web vision](http://sisinflab.poliba.it/publications/2017/RSILGPD17/), 32nd ACM SIGAPP Symposium On Applied Computing - apr 2017

References
------------
[1] M. Ruta, F. Scioscia, E. Di Sciascio, [Enabling the Semantic Web of Things: framework and architecture](http://sisinflab.poliba.it/publications/2012/RSD12/), 2012 IEEE Sixth International Conference on Semantic Computing

[2] M. Ruta, F. Scioscia, A. Pinto,  E. Di Sciascio, F. Gramegna, S. Ieva, G. Loseto, [Resource annotation, dissemination and discovery in the Semantic Web of Things: a CoAP-based framework](http://sisinflab.poliba.it/publications/2013/RSPDGIL13/), 2013 IEEE International Conference on Green Computing and Communications and IEEE Internet of Things and IEEE Cyber, Physical and Social Computing

[3] M. Ruta, F. Scioscia, G. Loseto, F. Gramegna, S. Ieva, E. Di Sciascio, [Mini-ME 2.0: powering the Semantic Web of Things](http://sisinflab.poliba.it/publications/2014/RSLGID14/), 3rd OWL Reasoner Evaluation Workshop (ORE 2014), Volume 1207, page 8--15 - jul 2014

[4] G. Loseto, M. Ruta, F. Scioscia, E. Di Sciascio, M. Mongiello, [Mining the user profile from a smartphone: a multimodal agent framework](http://sisinflab.poliba.it/publications/2013/LRSDM13/), 14th Workshop From Objects to Agents (WOA 2013), Volume 1099, page 47-53 - dec 2013

[5] M. Ruta, E. Di Sciascio, F. Scioscia, [Concept Abduction and Contraction in Semantic-based P2P Environments](http://sisinflab.poliba.it/publications/2011/RDS11/), Web Intelligence and Agent Systems, Volume 9, Number 3, page 179--207 - 2011

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

[sisinflab]: <http://sisinflab.poliba.it>
[GoogleIoT]: <http://googleresearch.blogspot.it/2016/02/announcing-google-internet-of-things.html>
[poliba]: <http://www.poliba.it>
[physicalweb]: <https://google.github.io/physical-web/>
[eddystone]: <https://github.com/google/eddystone>
[semphysicalweb]: <http://sisinflab.poliba.it/swottools/physicalweb>
[swot]: <http://sisinflab.poliba.it/swottools>
[minime]: <http://sisinflab.poliba.it/swottools/minime>

[basile]: <https://twitter.com/giob12>
[ieva]: <http://sisinflab.poliba.it/ieva>
[scioscia]: <http://sisinflab.poliba.it/scioscia>
[gramegna]: <http://sisinflab.poliba.it/gramegna>
