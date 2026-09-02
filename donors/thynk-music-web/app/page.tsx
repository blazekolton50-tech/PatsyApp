'use client';
import {useState,useRef,useEffect} from 'react';

const TRACKS = [
  {id:'vocals', name:'VOCALS', color:'#a855f7'},
  {id:'drums', name:'DRUMS', color:'#f97316'},
  {id:'bass', name:'BASS', color:'#22c55e'},
  {id:'synth', name:'SYNTH', color:'#3b82f6'},
];
const TABS = [
  {id:'mixer', label:'MIXER • 4-CH', col:'#a855f7'},
  {id:'dj', label:'DJ DECKS', col:'#06b6d4'},
  {id:'effects', label:'EFFECTS', col:'#ec4899'},
  {id:'eq', label:'EQUALISER', col:'#22c55e'},
  {id:'vocal', label:'VOCAL • AUTOTUNE', col:'#ef4444'},
  {id:'beats', label:'BEATS 16-PAD', col:'#f97316'},
  {id:'piano', label:'PIANO ROLL', col:'#8b5cf6'},
  {id:'ai', label:'AI TOOLS', col:'#eab308'},
  {id:'master', label:'MASTER', col:'#3b82f6'},
];

export default function Page(){
  const [clips,setClips]=useState([
    {id:1,track:'vocals',start:8,dur:58,name:'Lead Vox'},
    {id:2,track:'drums',start:2,dur:82,name:'Drum Loop'},
    {id:3,track:'bass',start:18,dur:72,name:'Bassline'},
    {id:4,track:'synth',start:38,dur:54,name:'Pad'},
  ]);
  const [active,setActive]=useState('mixer');
  const [playing,setPlaying]=useState(false);
  const [head,setHead]=useState(28);
  const [mixer,setMixer]=useState({ch1:62,ch2:71,ch3:58,ch4:66, cross:50, hi:{ch1:12,ch2:0,ch3:-2,ch4:4}, mid:{ch1:0,ch2:2,ch3:1,ch4:0}, low:{ch1:3,ch2:5,ch3:8,ch4:2}});
  const [effects,setEffects]=useState({reverb:72,echo:45,delay:30,flanger:60,phaser:28,chorus:35,dist:18,filter:42});
  const [autotune,setAutotune]=useState(62);
  const [projectId,setProjectId]=useState<string|null>(null);
  const tracksRef=useRef<HTMLDivElement>(null);
  const dragRef=useRef<any>(null);

  useEffect(()=>{ if(!playing) return; let r:number; const l=()=>{setHead(h=>(h+0.14)%100); r=requestAnimationFrame(l)}; r=requestAnimationFrame(l); return()=>cancelAnimationFrame(r); },[playing]);

  const saveProject = async()=>{
    const res = await fetch('/api/projects',{method:projectId?'PUT':'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify({id:projectId, name:`THYNK_${Date.now()}.thynk`, bpm:124, clips, mixer, effects})});
    const data = await res.json(); setProjectId(data.id);
  };

  const onDown = (e:any,id:number)=>{
    const rect=e.currentTarget.getBoundingClientRect();
    dragRef.current={id, offX:(e.touches? e.touches[0].clientX : e.clientX)-rect.left};
  };
  const onMove = (e:any)=>{
    if(!dragRef.current) return;
    const cx=e.touches? e.touches[0].clientX : e.clientX;
    const cy=e.touches? e.touches[0].clientY : e.clientY;
    const tr=tracksRef.current!.getBoundingClientRect();
    const laneW=tr.width-72;
    const ns=((cx-tr.left-72-dragRef.current.offX)/laneW)*210;
    const ti=Math.floor((cy-tr.top)/46);
    setClips(cs=>cs.map(c=> c.id===dragRef.current.id? {...c, start:Math.max(0,Math.min(195,ns)), track: TRACKS[Math.max(0,Math.min(3,ti))]?.id||c.track } : c));
  };
  const onUp = ()=>{ dragRef.current=null; };

  useEffect(()=>{ window.addEventListener('mousemove',onMove); window.addEventListener('touchmove',onMove,{passive:false}); window.addEventListener('mouseup',onUp); window.addEventListener('touchend',onUp); return()=>{window.removeEventListener('mousemove',onMove); window.removeEventListener('touchmove',onMove); window.removeEventListener('mouseup',onUp); window.removeEventListener('touchend',onUp);} });

  const playBeep = (f:number)=>{ const ctx=new (window.AudioContext||(window as any).webkitAudioContext)(); const o=ctx.createOscillator(); const g=ctx.createGain(); o.frequency.value=f; o.connect(g); g.connect(ctx.destination); g.gain.setValueAtTime(0.6,ctx.currentTime); g.gain.exponentialRampToValueAtTime(0.01,ctx.currentTime+0.3); o.start(); o.stop(ctx.currentTime+0.32); };

  return (
    <div className="h-screen w-screen max-w-[480px] mx-auto bg-black flex flex-col overflow-hidden rainbow">
      <div className="h-[56px] bg-[#08080b] flex items-center justify-center border-b border-white/5 shrink-0 relative">
        <div className="font-black text-[22px] tracking-tighter flex gap-[1px]">TH<span className="italic font-light text-[28px] -mx-[2px]">Y</span>NK<span className="text-[9px] ml-2 tracking-[0.35em] opacity-50 self-end mb-1">MUSIC</span></div>
        <button onClick={saveProject} className="absolute right-3 text-[9px] bg-white text-black px-3 py-1 rounded-full font-black">SAVE • 48kHz 24bit</button>
      </div>

      <div className="h-[30vh] min-h-[200px] bg-[#0a0a0f] flex flex-col shrink-0 border-b border-white/10 relative">
        <div className="flex items-center justify-between px-3 h-8 border-b border-white/5"><div className="text-[9px] opacity-50 tracking-widest">ARRANGEMENT • 0:00 / 3:00 • ALWAYS VISIBLE</div><button onClick={()=>setPlaying(!playing)} className={`px-3 py-1 rounded-full text-[10px] font-black ${playing?'bg-pink-500':'bg-white text-black'}`}>{playing?'■ STOP':'▶ PLAY'}</button></div>
        <div ref={tracksRef} className="flex-1 relative">
          {TRACKS.map(tr=><div key={tr.id} className="flex h-[46px] border-b border-white/[0.04]"><div className="w-[72px] flex items-center gap-1 px-2 border-r border-white/5"><div className="w-1 h-6 rounded-full" style={{background:tr.color}}></div><div className="text-[10px] font-black">{tr.name}</div></div><div className="flex-1 relative">{clips.filter(c=>c.track===tr.id).map(c=><div key={c.id} onMouseDown={e=>onDown(e,c.id)} onTouchStart={e=>onDown(e,c.id)} className="absolute top-1 h-9 rounded-[10px] flex items-center px-2 text-[10px] font-black text-white cursor-grab" style={{left:(c.start/210)*100+'%', width:(c.dur/210)*100+'%', background:`linear-gradient(90deg, ${tr.color}, ${tr.color}88)`}}>{c.name}</div>)}</div></div>)}
          <div className="absolute top-0 bottom-0 w-[2px] bg-white" style={{left:`calc(72px + ${head}% * (100% - 72px)/100)`}}></div>
        </div>
      </div>

      <div className="flex-1 bg-[#0f0f14] flex flex-col overflow-hidden">
        <div className="flex gap-1.5 p-2 overflow-x-auto border-b border-white/5">
          {TABS.map(t=><button key={t.id} onClick={()=>setActive(t.id)} className={`whitespace-nowrap px-4 py-2 rounded-full text-[10px] font-black ${active===t.id?'text-white':'bg-white/5 text-white/60'}`} style={active===t.id?{background:`linear-gradient(90deg, ${t.col}, #8b5cf6)`}:{}}>{t.label}</button>)}
        </div>
        <div className="flex-1 overflow-y-auto p-3">
          {active==='mixer' && <div className="grid grid-cols-4 gap-2">{TRACKS.map(tr=><div key={tr.id} className="bg-white/[0.03] rounded-[18px] border border-white/5 p-2 flex flex-col items-center"><div className="text-[10px] font-black" style={{color:tr.color}}>{tr.name}</div><div className="mt-2 w-[8px] h-[132px] bg-[#1a1a24] rounded-full relative"><div className="absolute w-8 h-5 bg-[#2a2a3e] border border-white/10 rounded-md left-1/2 -translate-x-1/2 shadow-md" style={{bottom: (mixer as any)[tr.id==='vocals'?'ch1':tr.id==='drums'?'ch2':tr.id==='bass'?'ch3':'ch4']+'%'}}></div></div><input type="range" className="mt-2 w-full accent-white" min={0} max={100} value={(mixer as any)[tr.id==='vocals'?'ch1':tr.id==='drums'?'ch2':tr.id==='bass'?'ch3':'ch4']} onChange={e=>setMixer({...mixer, [tr.id==='vocals'?'ch1':tr.id==='drums'?'ch2':tr.id==='bass'?'ch3':'ch4']: +e.target.value})}/></div>)}</div>}
          {active==='effects' && <div className="grid grid-cols-2 gap-2">{Object.entries(effects).map(([k,v])=><div key={k} className="bg-white/[0.03] rounded-[18px] border border-white/5 p-3"><div className="flex justify-between"><span className="text-[11px] font-black uppercase">{k}</span><span className="text-[10px] px-2 py-1 rounded-full bg-white/10">{v}% ON</span></div><input type="range" className="w-full mt-3 accent-pink-500" value={v as number} onChange={e=>setEffects({...effects, [k]:+e.target.value})}/></div>)}</div>}
          {active==='beats' && <div className="grid grid-cols-4 gap-2">{['KICK','SNARE','HIHAT','CLAP','VOCAL','RISE','STAB','FX','LOOP1','BASS','PERC','TOM','CHORD','BELL','SUB','VOX'].map((n,i)=><button key={n} onClick={()=>{playBeep(80+i*18); setClips(cs=>[...cs,{id:Date.now()+i,track: i%2?'drums':'bass', start: head/100*180, dur:14, name:n}])}} className="aspect-square rounded-[14px] font-black text-[10px] border" style={{background:['#ec489922','#3b82f622','#f9731622','#a855f722'][i%4], borderColor:['#ec4899','#3b82f6','#f97316','#a855f7'][i%4]+'66', color:['#ec4899','#3b82f6','#f97316','#a855f7'][i%4]}}>{n}</button>)}</div>}
          {active==='vocal' && <div className="bg-gradient-to-br from-pink-500/10 to-violet-500/10 rounded-[22px] border border-pink-500/20 p-4"><div className="text-[12px] font-black">🎙 MIC INPUT • AUTOTUNE {autotune}%</div><input type="range" className="w-full mt-3 accent-pink-500" value={autotune} onChange={e=>setAutotune(+e.target.value)}/><div className="flex gap-2 mt-3">{['NATURAL','ROBOTIC','T-PAIN','HARD'].map(m=><span key={m} className="text-[9px] px-3 py-1.5 rounded-full bg-white/10 font-bold">{m}</span>)}</div><button onClick={()=>setClips(cs=>[...cs,{id:Date.now(),track:'vocals',start:head/100*180,dur:40,name:'Vocal Take'}])} className="mt-4 w-full py-3 rounded-full bg-red-500 font-black animate-pulse">● REC • ADD TO SONG</button></div>}
          {active==='eq' && <div className="bg-white/[0.03] rounded-[18px] border border-white/5 p-4"><div className="text-[11px] font-black">EQUALISER • HI MID LOW • PARAMETRIC</div><svg viewBox="0 0 200 60" className="w-full h-24 mt-3"><path d="M0 50 Q 30 10 70 30 T 140 20 T 200 38" fill="none" stroke="url(#gg)" strokeWidth="2.5"/><defs><linearGradient id="gg"><stop offset="0%" stopColor="#ec4899"/><stop offset="100%" stopColor="#06b6d4"/></linearGradient></defs></svg><div className="grid grid-cols-3 gap-2 mt-3">{['HI +2.5dB','MID','LOW +3.0dB'].map(l=><div key={l} className="h-12 bg-white/5 rounded-full flex items-center justify-center text-[10px] font-bold">{l}</div>)}</div></div>}
          {active==='dj' && <div className="grid grid-cols-2 gap-2"><div className="bg-white/[0.03] rounded-[18px] p-3 border border-white/5"><div className="text-[10px] font-black text-center text-cyan-400">DECK A • 128BPM</div><div className="w-28 h-28 rounded-full bg-[#1a1a24] border border-white/10 mx-auto mt-2"></div></div><div className="bg-white/[0.03] rounded-[18px] p-3 border border-white/5"><div className="text-[10px] font-black text-center text-green-400">DECK B • 130BPM</div><div className="w-28 h-28 rounded-full bg-[#1a1a24] border border-white/10 mx-auto mt-2"></div></div></div>}
          {active==='ai' && <div className="grid grid-cols-2 gap-2">{['AI BEAT MAKER','STEM SEPARATOR','AI MASTERING','AI LYRICS'].map(t=><div key={t} className="bg-white/[0.03] rounded-[18px] p-3 border border-white/5"><div className="text-[11px] font-black">{t}</div><button className="mt-2 w-full py-2 rounded-full bg-gradient-to-r from-pink-500 to-violet-500 text-[10px] font-black" onClick={async()=>{ const r=await fetch('/api/stems',{method:'POST', body:JSON.stringify({projectId, type:'vocals'})}); const j=await r.json(); alert('Mock stems: '+JSON.stringify(j.stems.map((s:any)=>s.type))); }}>GENERATE</button></div>)}</div>}
          {active==='master' && <div className="space-y-3"><div className="bg-white/[0.03] rounded-[18px] p-3 border border-white/5 text-center"><div className="text-[10px] opacity-50">LUFS</div><div className="text-xl font-black text-green-400">-14.2 LUFS</div></div><button className="w-full py-3 rounded-full bg-gradient-to-r from-pink-500 to-cyan-400 font-black" onClick={async()=>{ const r=await fetch('/api/export',{method:'POST', body:JSON.stringify({projectId, format:'wav'})}); const j=await r.json(); alert('Mock export reference: '+j.url); }}>↓ EXPORT NOW • WAV 24bit/48kHz</button></div>}
        </div>
      </div>
    </div>
  );
}