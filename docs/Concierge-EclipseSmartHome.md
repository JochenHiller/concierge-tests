# Eclipse SmartHome running on Concierge

### Bugs identified

* ~~[#439182 org.osgi.service.condpermadmin package is missing in Concierge](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439182)~~ (Closed)
  * Note: added an extension bundle which provides the missing classes. This needs to be installed first when missing classes are needed (e.g. for Equinox console).
* ~~[#439180 org.eclipse.equinox.console bundle has wrong version 1.1.0 for "Import-Package: org.osgi.framework.namespace"](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180)~~ (Closed, since MarsM3)
  * see also ~~https://bugs.eclipse.org/bugs/show_bug.cgi?id=438783~~ (Rejected by Concierge)
* ~~[#439445 Equinox console bundle has hard dependency to Equinox framework and not to supplement bundle](https://bugs.eclipse.org/bugs/show_bug.cgi?id=439445)~~ (Closed, since MarsM3)

## TODO

* Link auf start_concierge_debug.sh hinterlegen
* Bugs: https://bugs.eclipse.org/bugs/show_bug.cgi?id=439180
