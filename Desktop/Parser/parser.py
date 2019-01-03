'''
import aspell


def fixPar(par, speller):
    words = par.split()
    words[:] = [fixWord(x, s) for x in words]
    par = ""
    for word in words:
        par += word + " "
    return par

def fixWord(word, s):
    speller, enc = s
    wordEncoded = word.decode('utf-8').encode(enc)
    if wordEncoded in speller:
        return word
    else:
        suggestion = speller.suggest(wordEncoded)
        if len(suggestion) == 0:
            return word
        else:
            return suggestion[0].decode(enc).encode('utf-8')

def initSpeller():
    s = aspell.Speller('lang', 'tr')
    enc = s.ConfigKeys()['encoding'][1]
    print enc
    return(s, enc)
'''

from xlsxwriter.workbook import Workbook

# Create an new Excel file and add a worksheet.
workbook = Workbook('wrappedPars.xlsx')
worksheet = workbook.add_worksheet()

# Widen the first column to make the text clearer.
worksheet.set_column('A:A', 70)

# Add a cell format with text wrap on.
cell_format = workbook.add_format({'text_wrap': True})


with open ('rawtext.txt', 'r') as file:
    data = file.read()
    paragraphs = data.decode('utf-8').splitlines()
    pars = []
    for par in paragraphs:
        if len(par) > 250 and len(par) < 750:
            pars.append(par)
        if par=="1992-1994":
            break
    print len(pars)

count = 0

for text in pars:
    count += 1
    if count == 4949:
        break
    # Write a wrapped string to a cell.
    print 'A' + str(count)
    worksheet.write('A' + str(count), pars[count], cell_format)


print pars[-1]
workbook.close()

# with open("texts.csv", "wb") as f:
#    writer = csv.writer(f)
#    writer.writerows(pars)


#    s = initSpeller()

#    pars[:] = [fixPar(x, s) for x in pars]
#    print pars[-1]
#    print ""
#    print fixPar(pars[-1], s)
